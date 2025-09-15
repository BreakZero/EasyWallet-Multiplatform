SHELL := /bin/bash

push:
	git tag $(tag)
	git push origin $(tag)

