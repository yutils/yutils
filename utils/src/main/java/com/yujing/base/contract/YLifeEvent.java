package com.yujing.base.contract;

/**
 * 生命周期事件
 */
public enum YLifeEvent {
    //public
    onCreate,
    onActivityResult,
    onConfigurationChanged,
    onRequestPermissionsResult,
    onPause,
    onStart,
    onResume,
    onRestart,
    onStop,
    onDestroy,
    //activity
    onKeyDown,
    onNewIntent,
    onBackPressed,
    finish,
    //fragment
    onHiddenChanged,
    onViewCreated,
    onDestroyView,
    onDetach,
}
