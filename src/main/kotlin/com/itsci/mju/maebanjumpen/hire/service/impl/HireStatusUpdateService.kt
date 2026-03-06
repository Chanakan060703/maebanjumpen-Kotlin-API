package com.itsci.mju.maebanjumpen.hire.service.impl

import com.itsci.mju.maebanjumpen.hire.constant.JobStatusEnum
import com.itsci.mju.maebanjumpen.hire.repository.HireRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime

@Service
class HireStatusUpdateService @Autowired internal constructor(
    private val hireRepository: HireRepository,
    private val taskScheduler: TaskScheduler
) {

    @Transactional
    fun revertStatus(hireId: Long) {
        val hireToUpdate = hireRepository.findById(hireId).orElse(null)

        val latestStatus = hireToUpdate?.jobStatus

        val shouldRevert = JobStatusEnum.REPORTED.value.equals(latestStatus, ignoreCase = true)

        if (hireToUpdate != null && shouldRevert) {
            hireToUpdate.jobStatus = JobStatusEnum.COMPLETED.value
            hireRepository.save(hireToUpdate)
            println("✅ Hire ID $hireId status reverted from '$latestStatus' to '${JobStatusEnum.COMPLETED.value}' at ${LocalDateTime.now()}")
        } else {
            if (hireToUpdate == null) {
                System.err.println("Hire ID $hireId not found when attempting to revert status.")
            } else {
                println("⚠️ Hire ID $hireId status was not eligible for revert (Current: $latestStatus). No revert performed.")
            }
        }
    }

    fun scheduleStatusRevert(hireId: Long, delayInSeconds: Long) {
        println("⏳ Scheduled Hire ID $hireId to revert status to '${JobStatusEnum.COMPLETED.value}' in $delayInSeconds seconds.")

        taskScheduler.schedule({
            try {
                revertStatus(hireId)
            } catch (e: Exception) {
                System.err.println("❌ Error reverting status for Hire ID $hireId: ${e.message}")
                e.printStackTrace()
            }
        }, Instant.now().plusSeconds(delayInSeconds))
    }
}

