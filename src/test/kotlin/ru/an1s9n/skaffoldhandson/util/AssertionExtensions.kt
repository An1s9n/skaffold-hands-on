package ru.an1s9n.skaffoldhandson.util

import org.assertj.db.api.TableAssert
import org.assertj.db.api.TableRowAssert

fun TableAssert.row(index: Int, check: TableRowAssert.() -> Unit): TableAssert {
  row(index).check()
  return this
}
