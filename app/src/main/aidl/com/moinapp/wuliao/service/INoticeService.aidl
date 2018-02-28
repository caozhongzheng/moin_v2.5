package com.moinapp.wuliao.service;

interface INoticeService
{ 
   void scheduleNotice();
   void requestNotice();
   void clearNotice(int uid,int type);
}