package com.example.mywallet.data
import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mywallet.dao.InstallmentDao
import com.example.mywallet.dao.SubscriptionDao
import com.example.mywallet.data.model.Installment
import com.example.mywallet.data.model.Subscription


@Database(
    entities = [Subscription::class, Installment::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun installmentDao(): InstallmentDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mywallet_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}