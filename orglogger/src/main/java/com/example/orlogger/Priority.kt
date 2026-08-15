package com.youssefmkh.logger

import androidx.annotation.IntDef


@IntDef(
    Priority.INFO,
    Priority.DEBUG,
    Priority.VERBOSE,
    Priority.WARN,
    Priority.ERROR,
    Priority.ASSERT
)

/**
 * All log level known for this library and their priority from
 * the most important to the least important
 */

@Target(AnnotationTarget.TYPE)
annotation class Priority{

    companion object{
        const val VERBOSE: Int = 2
        const val DEBUG: Int = 3
        const val INFO: Int = 4
        const val WARN: Int = 5
        const val ERROR: Int = 6
        const val ASSERT: Int = 7

        fun toString( priority:@Priority Int): String{
            return when(priority){
                VERBOSE-> "VERBOSE"
                DEBUG-> "DEBUG"
                INFO->"INFO"
                WARN->"WARN"
                ERROR->"ERROR"
                else->"ASSERT"
            }
        }
    }
}
