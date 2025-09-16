#!/usr/bin/env bash
# git-prune-local.sh — Delete unused local branches and/or tags.
# What it does (by default does both branches and tags):
#   • Branches: delete local branches that are fully merged into MAIN, and branches whose upstream is gone.
#   • Tags: delete tags that exist only locally (not on the remote).
#
# Usage:
#   ./git-prune-local.sh [options]
#
# Options:
#   --branches            Only prune branches.
#   --tags                Only prune tags.
#   -m, --main BRANCH     MAIN branch name (auto-detect if omitted).
#   -r, --remote NAME     Remote name (default: origin, or first available).
#   -n, --dry-run         Show actions without deleting.
#   -f, --force           Force delete branches (-D instead of -d).
#   --keep REGEX          Protect local branches matching REGEX (can repeat).
#   --tags-pattern REGEX  Only delete local-only tags matching REGEX.
#   -h, --help            Show help.
#
set -euo pipefail

MODE="both"   # both|branches|tags
MAIN=""
REMOTE=""
DRY_RUN=0
FORCE=0
KEEP_REGEXES=()
TAGS_PATTERN=""

log() { printf "➜ %s\n" "$*"; }
warn() { printf "⚠︎ %s\n" "$*" >&2; }
die() { printf "✖ %s\n" "$*" >&2; exit 1; }
usage() { sed -n '1,80p' "$0"; exit 0; }

run() {
  if [[ $DRY_RUN -eq 1 ]]; then
    printf "[dry-run] %s\n" "$*"
  else
    eval "$@"
  fi
}

# Parse args
while [[ $# -gt 0 ]]; do
  case "$1" in
    --branches) MODE="branches"; shift;;
    --tags) MODE="tags"; shift;;
    -m|--main) MAIN="${2:-}"; shift 2;;
    -r|--remote) REMOTE="${2:-}"; shift 2;;
    -n|--dry-run) DRY_RUN=1; shift;;
    -f|--force) FORCE=1; shift;;
    --keep) KEEP_REGEXES+=("${2:-}"); shift 2;;
    --tags-pattern) TAGS_PATTERN="${2:-}"; shift 2;;
    -h|--help) usage;;
    *) warn "Unknown argument: $1"; usage;;
  esac
done

# Ensure Git repo
git rev-parse --is-inside-work-tree >/dev/null 2>&1 || die "Not inside a Git repository."

# Decide REMOTE
if [[ -z "${REMOTE}" ]]; then
  if git remote get-url origin >/dev/null 2>&1; then
    REMOTE="origin"
  else
    REMOTE="$(git remote | head -n1)"
    [[ -n "$REMOTE" ]] || die "No remotes configured."
  fi
fi

# Fetch & prune (detect --prune-tags support)
fetch_and_prune() {
  if git fetch -h 2>&1 | grep -q -- '--prune-tags'; then
    run "git fetch --all --prune --prune-tags --tags"
  else
    run "git fetch --all --prune --tags"
  fi
  run "git remote prune "$REMOTE""
}

# Detect MAIN if not provided
detect_main() {
  local m
  m="$(git symbolic-ref --quiet --short "refs/remotes/${REMOTE}/HEAD" 2>/dev/null || true)"
  if [[ -n "$m" ]]; then m="${m#${REMOTE}/}"; fi
  if [[ -z "$m" ]]; then
    for c in main master develop; do
      if git show-ref --verify --quiet "refs/heads/${c}"; then m="$c"; break; fi
    done
  fi
  if [[ -z "$m" ]]; then m="$(git rev-parse --abbrev-ref HEAD)"; fi
  printf "%s" "$m"
}

# Build branch protect regex
protect_re() {
  local base="^(main|master|develop)$"
  if [[ -n "$MAIN" && ! "$MAIN" =~ ^(main|master|develop)$ ]]; then
    printf "^(${MAIN}|main|master|develop)$"
  else
    printf "%s" "$base"
  fi
}

# Delete a branch
delete_branch() {
  local br="$1"
  local current
  current="$(git rev-parse --abbrev-ref HEAD)"
  if [[ "$br" == "$current" ]]; then
    warn "Skip current branch: $br"
    return 0
  fi
  if [[ $FORCE -eq 1 ]]; then
    run "git branch -D "$br""
  else
    run "git branch -d "$br""
  fi
}

ORIGINAL="$(git rev-parse --abbrev-ref HEAD || echo '')"
trap 'if [[ -n "$ORIGINAL" ]]; then git rev-parse --abbrev-ref HEAD >/dev/null 2>&1 && [[ "$(git rev-parse --abbrev-ref HEAD)" != "$ORIGINAL" ]] && run "git checkout "$ORIGINAL"" || true; fi' EXIT

fetch_and_prune

if [[ -z "$MAIN" ]]; then MAIN="$(detect_main)"; fi
log "Main branch: $MAIN (remote: $REMOTE)"

# Fast-forward MAIN if exists locally
if git show-ref --verify --quiet "refs/heads/${MAIN}"; then
  if [[ "$ORIGINAL" != "$MAIN" ]]; then run "git checkout "$MAIN""; fi
  run "git pull --ff-only "$REMOTE" "$MAIN"" || warn "Could not fast-forward $MAIN; continue."
  if [[ "$ORIGINAL" != "$MAIN" ]]; then run "git checkout "$ORIGINAL""; fi
fi

PROTECT="$(protect_re)"

# Branch pruning
if [[ "$MODE" == "both" || "$MODE" == "branches" ]]; then
  log "Pruning local branches…"
  merged="$(git branch --format '%(refname:short)' --merged "$MAIN" || true)"
  if [[ -n "$merged" ]]; then
    # filter protected and keeps
    filtered="$(printf "%s\n" "$merged" | grep -E -v "$PROTECT" || true)"
    if [[ -n "$filtered" && "${#KEEP_REGEXES[@]:-0}" -gt 0 ]]; then
      for k in "${KEEP_REGEXES[@]}"; do
        filtered="$(printf "%s\n" "$filtered" | grep -E -v "$k" || true)"
      done
    fi
    if [[ -n "$filtered" ]]; then
      log "Deleting merged branches:"
      printf "%s\n" "$filtered" | while IFS= read -r b; do [[ -n "$b" ]] && delete_branch "$b"; done
    else
      log "No merged branches to delete."
    fi
  else
    log "No merged branches."
  fi

  gone="$(git branch -vv | awk '/: gone]/{print $1}' || true)"
  if [[ -n "$gone" ]]; then
    filtered="$(printf "%s\n" "$gone" | grep -E -v "$PROTECT" || true)"
    if [[ -n "$filtered" && "${#KEEP_REGEXES[@]:-0}" -gt 0 ]]; then
      for k in "${KEEP_REGEXES[@]}"; do
        filtered="$(printf "%s\n" "$filtered" | grep -E -v "$k" || true)"
      done
    fi
    if [[ -n "$filtered" ]]; then
      log "Deleting branches with gone upstream:"
      printf "%s\n" "$filtered" | while IFS= read -r b; do [[ -n "$b" ]] && delete_branch "$b"; done
    else
      log "No 'gone upstream' branches to delete."
    fi
  else
    log "No branches with gone upstream."
  fi
fi

# Tag pruning
if [[ "$MODE" == "both" || "$MODE" == "tags" ]]; then
  log "Pruning local-only tags…"
  fetch_and_prune
  local_tags="$(git tag | LC_ALL=C sort -u || true)"
  remote_tags="$(git ls-remote --tags "$REMOTE" | awk '{print $2}' | sed 's|^refs/tags/||; s/\^{}$//' | LC_ALL=C sort -u || true)"
  if [[ -z "$local_tags" ]]; then
    log "No local tags."
  else
    local_only="$(comm -23 <(printf "%s\n" "$local_tags") <(printf "%s\n" "$remote_tags") || true)"
    if [[ -n "$TAGS_PATTERN" && -n "$local_only" ]]; then
      local_only="$(printf "%s\n" "$local_only" | grep -E "$TAGS_PATTERN" || true)"
    fi
    if [[ -n "$local_only" ]]; then
      log "Deleting local-only tags:"
      printf "%s\n" "$local_only" | while IFS= read -r t; do [[ -n "$t" ]] && run "git tag -d "$t""; done
    else
      log "No local-only tags to delete."
    fi
  fi
fi

log "Done."
