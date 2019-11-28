package cn.daqinjia.android.scaffold.demo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * # Config
 * Created on 2019/11/25
 *
 * @author Vove
 */
@Entity(tableName = "configs")
data class Config(
    @PrimaryKey
    val key: String,
    val value: String,
    val enabled: Boolean = true
)