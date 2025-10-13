package com.example.whatsapp.di

import android.content.Context
import androidx.room.Room
import com.example.whatsapp.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWhatsAppDatabase(@ApplicationContext context: Context): WhatsAppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WhatsAppDatabase::class.java,
            "whatsapp_database"
        ).build()
    }

    @Provides
    fun provideUserDao(database: WhatsAppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideConversationDao(database: WhatsAppDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    fun provideMessageDao(database: WhatsAppDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    fun provideStatusDao(database: WhatsAppDatabase): StatusDao {
        return database.statusDao()
    }

    @Provides
    fun provideCallDao(database: WhatsAppDatabase): CallDao {
        return database.callDao()
    }

    @Provides
    fun provideCommunityDao(database: WhatsAppDatabase): CommunityDao {
        return database.communityDao()
    }
}