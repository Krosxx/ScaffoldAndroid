package cn.vove7.android.scaffold.demo.repo

import kotlinx.coroutines.delay

/**
 * # ImagesRepo
 *
 * Created on 2019/12/23
 * @author Vove
 */
class ImagesRepo {

    suspend fun getImages(): List<String> {
        delay(3000)
        return listOf(
            "photo-1558981001-1995369a39cd",
            "flagged/photo-1575556809963-3d9e5730eda0",
            "photo-1575483893529-3a9a8f8f2da5",
            "photo-1575490937262-344efe96d264",
            "photo-1575552141920-05e428c50bf3",
            "photo-1575525351638-776e90838483",
            "http://wx4.sinaimg.cn/orj360/007kVUJhly1g1inspvu5zj30v93rbnpd.jpg",
            "photo-1558981408-db0ecd8a1ee4",
            "photo-1576049278477-cd1e66af49be",
            "photo-1551045820-6a77c4a0ce25",
            "photo-1550983545-35aff7acb744",
            "photo-1550998469-4b77fc2973d3",
            "photo-1550979068-47f8ec0c92d0",
            "photo-1544794577-7ba77ea55507",
            "photo-1537204696486-967f1b7198c8",
            "photo-1535985664332-bd93fc895508",
            "photo-1508763718304-b2ddedcc2e88",
            "photo-1420641519650-9d7d0ea0a829",
            "photo-1416103292956-1f6aff3d93fb",
            "photo-1460474684596-3c6d3f1abb96",
            "photo-1519304666391-ffad48fe778a",
            "photo-1499455631844-d77c223bca19",
            "photo-1541691200587-2045c1c47483",
            "photo-1515412512744-6e4adc8b5e55",
            "photo-1467003909585-2f8a72700288",
            "photo-1490818387583-1baba5e638af",
            "photo-1478369402113-1fd53f17e8b4",
            "photo-1484300681262-5cca666b0954"
        ).map {
            if (it.startsWith("http")) {
                it
            } else {
                "https://images.unsplash.com/$it?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
            }
        }
    }
}