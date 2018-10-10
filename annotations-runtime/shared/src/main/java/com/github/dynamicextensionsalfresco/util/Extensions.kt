package com.github.dynamicextensionsalfresco.util

import org.springframework.util.StringUtils

fun String?.hasText() = this != null && StringUtils.hasText(this)