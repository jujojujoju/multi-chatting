#ifndef __CTHREAD_H__
#define __CTHREAD_H__


#include <iostream>
#include <stdio.h>
#include <pthread.h>
#include <signal.h>
#include "CThreadException.h"

using std::cout;
using std::endl;

class CThread {

private:
    pthread_t threadId;

    void createThread() throw (CThreadException);
    static void* ThreadFunc(void *ptr);

public:

    CThread();
    
    virtual ~CThread();

    virtual void run()=0;

    void start() throw (CThreadException);
    void join() throw (CThreadException);
    bool isRunning() throw();
};



#endif
