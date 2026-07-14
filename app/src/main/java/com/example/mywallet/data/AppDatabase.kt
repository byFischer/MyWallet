package com.example.mywallet.data
import android.content.Context
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mywallet.dao.InstallmentDao
import com.example.mywallet.dao.SubscriptionDao
import com.example.mywallet.data.model.Installment
import com.example.mywallet.data.model.Subscription


@Database(
    entities = [Subscription::class, Installment::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun installmentDao(): InstallmentDao

    companion object{
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE subscriptions ADD COLUMN providerDomain TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE subscriptions ADD COLUMN providerBrandId TEXT DEFAULT NULL")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mywallet_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
