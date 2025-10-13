package com.example.whatsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "calls")
data class Call(
    @PrimaryKey
    val id: String = "",
    val callerId: String = "",
    val callerName: String = "",
    val callerAvatarUrl: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val receiverAvatarUrl: String = "",
    val type: CallType = CallType.VOICE,
    val status: CallStatus = CallStatus.INITIATED,
    val startTime: Date = Date(),
    val endTime: Date? = null,
    val duration: Long = 0, // in milliseconds
    val isGroupCall: Boolean = false,
    val participants: List<CallParticipant> = emptyList(),
    val conversationId: String? = null
)

data class CallParticipant(
    val userId: String = "",
    val userName: String = "",
    val joinedAt: Date? = null,
    val leftAt: Date? = null,
    val isMuted: Boolean = false,
    val isVideoEnabled: Boolean = true
)

enum class CallType {
    VOICE, VIDEO
}

enum class CallStatus {
    INITIATED, RINGING, ANSWERED, DECLINED, MISSED, ENDED, FAILED
}