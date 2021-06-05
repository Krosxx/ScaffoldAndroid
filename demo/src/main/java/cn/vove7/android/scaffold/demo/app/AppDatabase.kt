/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.vove7.android.scaffold.demo.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.vove7.android.scaffold.app.ScaffoldApp
import cn.vove7.android.scaffold.demo.data.Config
import cn.vove7.android.scaffold.demo.data.ConfigDao

/**
 * The Room database for this app
 */
@Suppress("MemberVisibilityCanBePrivate")
@Database(entities = [Config::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configdDao(): ConfigDao

    companion object {
        private const val DATABASE_NAME = "demo_db"

        val INSTANCE by lazy {
            buildDatabase(ScaffoldApp.APP)
        }

        val configdDao get() = INSTANCE.configdDao()

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context, AppDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
