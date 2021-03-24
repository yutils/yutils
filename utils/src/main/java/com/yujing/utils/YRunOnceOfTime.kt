package com.yujing.utils

/**
 * time内只允许运行一次，多余的事件直接抛弃
 */
/*举例
//这句语音每20秒只能说一次
fun speakOnce(str:String){
    YRunOnceOfTime.run(20000,str){
        speak("语音播报："+str)
    }
}
*/
class YRunOnceOfTime(var time: Long, var tag: String, var lastTime: Long) {
    companion object {
        val list: MutableList<YRunOnceOfTime> = ArrayList()

        fun clear() {
            list.clear()
        }

        fun remove(tag: String) {
            val mIterator = list.iterator()
            while (mIterator.hasNext()) {
                val item = mIterator.next()
                if (tag == item.tag) mIterator.remove()
            }
        }

        fun removeContains(tag: String) {
            val mIterator = list.iterator()
            while (mIterator.hasNext()) {
                val item = mIterator.next()
                if (item.tag.contains(tag)) mIterator.remove()
            }
        }

        fun addTag(time: Long, tag: String) {
            var find = false
            for (item in list) if (item.tag == tag) {
                find = true
                item.time = time
            }
            if (!find) {
                list.add(YRunOnceOfTime(time, tag, System.currentTimeMillis()))
            }
        }

        @Synchronized
        fun run(time: Long, tag: String, runnable: Runnable?) {
            //移除过期对象后，找列表中是否有这个tag,如果有，就不执行，没有就执行并且记录
            //移除过期对象
            val currentTime = System.currentTimeMillis()
            val mIterator = list.iterator()
            while (mIterator.hasNext()) {
                val item = mIterator.next()
                if (currentTime - item.lastTime > item.time) mIterator.remove()
            }

            //找有没有对应tag，如果找到，find就标记true
            var find = false
            for (item in list) if (item.tag == tag) find = true

            //如果没有找到对象，就创建并且运行
            if (!find) {
                runnable?.run()
                list.add(YRunOnceOfTime(time, tag, System.currentTimeMillis()))
            }
        }
    }
}
