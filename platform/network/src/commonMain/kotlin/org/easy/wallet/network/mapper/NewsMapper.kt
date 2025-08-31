package org.easy.wallet.network.mapper

import org.easy.wallet.model.News
import org.easy.wallet.network.model.dto.BlockChairNewsDto

internal fun BlockChairNewsDto.toNews(): News = News(
  title = this.title,
  source = source,
  language = language,
  link = link,
  time = time,
  hash = hash,
  description = description,
  tags = tags
)