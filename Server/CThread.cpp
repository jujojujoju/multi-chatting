#include "CThread.h"
    

CThread::CThread(): threadId((pthread_t)0) {

}

CThread::~CThread() {
    try {
        join();
    } catch (...) {}
}

void CThread::start() throw(CThreadException) {
    createThread();
}

void CThread::join() throw(CThreadException) {
    if (pthread_equal(threadId, (pthread_t)0)) {
        return;
    }
    
    int rc = pthread_join(threadId, NULL);
    if (rc != 0) {
        throw CThreadException(2);
    }
    
    threadId = (pthread_t)0;
}

bool CThread::isRunning() throw() {
    if (pthread_equal(threadId, (pthread_t)0)) {
        return false;
    }

    if (pthread_kill(threadId, 0) == 0){
        return true;
    } else {
        return false;
    }
}

void* CThread::ThreadFunc(void *arg) {
    CThread *cthread = static_cast<CThread*>(arg);
    cthread->run();
    pthread_exit(0);
}

void CThread::createThread() throw(CThreadException) {
    int rc = pthread_create(&threadId, NULL, ThreadFunc, this);
    if (rc != 0){
        throw CThreadException(1);
    }
}


