package org.solidhax.apostle.utils.scheduler

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object TickScheduler {
    private val queuedTasks = ArrayDeque<ScheduledTask>()

    fun init() {
        ClientTickEvents.END_WORLD_TICK.register { tick() }
    }

    fun schedule(ticks: Int, task: () -> Unit) {
        require(ticks >= 0) { "ticks must be >= 0." }
        queuedTasks.addLast(ScheduledTask(ticks, task))
    }

    private fun tick() {
        if(queuedTasks.isEmpty()) return

        val tasksToProcess = queuedTasks.size
        repeat(tasksToProcess) {
            val scheduledTask = queuedTasks.removeFirst()
            if(scheduledTask.remainingTicks <= 0)
                scheduledTask.task()
            else
                queuedTasks.addLast(scheduledTask.copy(remainingTicks = scheduledTask.remainingTicks - 1))
        }
    }

    private data class ScheduledTask(
        val remainingTicks: Int,
        val task: () -> Unit,
    )
}

fun schedule(ticks: Int, task: () -> Unit) {
    TickScheduler.schedule(ticks, task)
}