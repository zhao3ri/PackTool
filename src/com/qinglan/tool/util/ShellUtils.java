package com.qinglan.tool.util;

import com.qinglan.common.Log;

import java.io.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static com.qinglan.tool.util.Utils.isEmpty;

public class ShellUtils {
    final CyclicBarrier barrier = new CyclicBarrier(2);
    ProgressListener onProgressListener;

    public ShellUtils() {
    }

    public ShellUtils(ProgressListener listener) {
        onProgressListener = listener;
    }

    public int execute(String cmd) {
        Process ps = null;
        int result = -1;
        barrier.reset();
        try {
            ps = Runtime.getRuntime().exec(cmd);
            Log.iln("Number of parties required to trip the barrier = " + barrier.getParties());
            StreamGobbler errorGobbler = new StreamGobbler(ps.getErrorStream());
            StreamGobbler outGobbler = new StreamGobbler(ps.getInputStream());
            errorGobbler.setTag("Error:");
            outGobbler.setTag("Out:");
            errorGobbler.start();
            outGobbler.start();
            barrier.await();
            ps.waitFor();

            result = ps.exitValue();
            ps.destroy();
            ps = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        return result;
    }

    class StreamGobbler extends Thread {
        InputStream is;
        OutputStream os;
        String tag = "";

        StreamGobbler(InputStream is) {
            this(is, null);
        }

        StreamGobbler(InputStream is, OutputStream redirect) {
            this.is = is;
            this.os = redirect;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public void run() {
            InputStreamReader isr = null;
            BufferedReader br = null;
            PrintWriter pw = null;
            Log.iln(tag + " Is the barrier broken? - " + barrier.isBroken());
            try {
                if (os != null) {
                    pw = new PrintWriter(os);
                }
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                String line;
                while (!isEmpty((line = br.readLine()))) {
                    if (pw != null) {
                        pw.println(line);
                    }
                    if (onProgressListener != null) {
                        onProgressListener.publishProgress(line);
                    }
                    Log.iln(tag + line);
                }
                Log.iln((tag + " Number of parties waiting at the barrier at this point = " + barrier.getNumberWaiting()));
                if (pw != null) {
                    pw.flush();
                }
                barrier.await();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (InterruptedException | BrokenBarrierException e) {
                Log.eln(tag + " " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (pw != null)
                        pw.close();
                    if (br != null)
                        br.close();
                    if (isr != null)
                        isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface ProgressListener {
        void publishProgress(String values);
    }
}