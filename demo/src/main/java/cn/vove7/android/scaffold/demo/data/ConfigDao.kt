package cn.vove7.android.scaffold.demo.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * # ConfigDao
 * Created on 2019/11/25
 *
 * @author Vove
 */
@Dao
interface ConfigDao {

    @Query("select * from configs where `key` = :key and enabled")
    fun get(key: String): LiveData<Config>


    /**
     * 覆盖
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(config: Config)
}

operator fun ConfigDao.get(key: String): LiveData<Config> = get(key)

operator fun ConfigDao.set(key: String, value: String) = GlobalScope.launch {
    insert(Config(key, value))
}