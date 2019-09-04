//package io.metadew.iesi.server.execution.requestor;
//
//public class RequestorRunnable implements Runnable {
//
//    public RequestorRunnable() {
//    }
//
//    // @Override
//    public void run() {
//
//        int interval = Integer.parseInt("1000");
//
//        RequestorService requestServer = new RequestorService();
//
//        int i = 1;
//        while (i == 1) {
//            try {
//                Thread.sleep(interval);
//            } catch (InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
//
//            if (requestServer.execListen()) {
//                requestServer.execute();
//            }
//        }
//    }
//
//}