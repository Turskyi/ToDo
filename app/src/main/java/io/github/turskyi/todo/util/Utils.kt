package io.github.turskyi.todo.util

/** turns statement into expression and make it compile safety */
val <T> T.exhaustive: T
    get() = this