package com.yujing.utils

/**
 * time（millisecond）内只允许运行一次，多余的事件直接抛弃
 * @author 余静 2021年4月9日10:07:39
 */
/*举例
//这句语音每20秒只能说一次
fun speakOnce(str:String){
    YRunOnceOfTime.run(1000*20,str){
        speak("语音播报："+str)
    }
}

//1秒内只能运行一次
if (YRunOnceOfTime.check("tag1")) {
    YLog.i("运行内容")
    YRunOnceOfTime.addTag(1000, "tag1")
}

//1秒内只能运行一次，防抖
if (YRunOnceOfTime.check(1000,"tag1")) {
    YLog.i("运行内容")
}
*/
class YRunOnceOfTime(var time: Long, var tag: String, var lastTime: Long) {
    companion object {
        private val list: MutableList<YRunOnceOfTime> = ArrayList()

        /**
         * 清除全部
         */
        fun clear() {
            list.clear()
        }

        /**
         * 移除指定tag
         */
        fun remove(tag: String) {
            val mIterator = list.iterator()
            while (mIterator.hasNext()) {
                val item = mIterator.next()
                if (tag == item.tag) mIterator.remove()
            }
        }

        /**
         * 移除包含关键字的tag
         */
        fun removeContains(tag: String) {
            val mIterator = list.iterator()
            while (mIterator.hasNext()) {
                val item = mIterator.next()
                if (item.tag.contains(tag)) mIterator.remove()
            }
        }

        /**
         * 添加一个tag，如果已经存在就不添加
         */
        fun addTag(time: Long, tag: String) {
            var find = false
            for (item in list)
                if (item.tag == tag) {
                    find = true
                    item.time = time
                }
            if (!find) {
                list.add(YRunOnceOfTime(time, tag, System.currentTimeMillis()))
            }
        }

        /**
         * 更新或添加一个tag，会更新最后一次时间
         */
        fun updateTag(time: Long, tag: String) {
            var find = false
            for (item in list)
                if (item.tag == tag) {
                    find = true
                    item.time = time
                    item.lastTime = System.currentTimeMillis()
                }
            if (!find) {
                list.add(YRunOnceOfTime(time, tag, System.currentTimeMillis()))
            }
        }

        /**
         * tag能否使用
         * 不存在或者过期,就代表tag可以使用，返回true
         */
        fun check(tag: String): Boolean {
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
            return !find
        }

        /**
         * tag能否使用，time时间内只能运行一次
         * 检查并且添加一个tag,time时间内只返回一次true，可以做成防抖
         */
        /*
            //1秒内只能运行一次，防抖
            if (YRunOnceOfTime.check(1000,"tag1")) {
                //运行内容
                YLog.i("运行内容")
            }
         */
        fun check(time: Long, tag: String): Boolean {
            val check = check(tag)
            addTag(time, tag)
            return check
        }

        /**
         * tag能否使用，与上一次至少间隔time时间
         * 检查并且添加一个tag,time秒内只返回一次true，可以做成防抖
         */
        /*
            //与上一次至少间隔1秒
            if (YRunOnceOfTime.checkUpdate(1000,"tag1")) {
                //运行内容
                YLog.i("运行内容")
            }
         */
        fun checkUpdate(time: Long, tag: String): Boolean {
            val check = check(tag)
            updateTag(time, tag)
            return check
        }

        /**
         * 运行，time内只允许运行一次，多余的事件直接抛弃
         */
        @Synchronized
        fun run(time: Long, tag: String, runnable: Runnable?) {
            //如果没有找到对象，就创建并且运行
            if (check(tag)) {
                runnable?.run()
                list.add(YRunOnceOfTime(time, tag, System.currentTimeMillis()))
            }
        }

        /**
         * 运行，与上一次至少间隔time时间，多余的事件直接抛弃
         */
        @Synchronized
        fun runUpdate(time: Long, tag: String, runnable: Runnable?) {
            //如果没有找到对象，就创建并且运行
            if (checkUpdate(time, tag)) {
                runnable?.run()
                list.add(YRunOnceOfTime(time, tag, System.currentTimeMillis()))
            }
        }
    }
}
