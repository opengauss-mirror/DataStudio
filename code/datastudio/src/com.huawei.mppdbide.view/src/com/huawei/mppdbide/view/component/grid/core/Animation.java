/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.view.component.grid.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: The Class Animation
 *
 * @since 3.0.0
 */
public class Animation {
    /** 
     * The display composite.
     */
    private Composite displayComposite;

    /** 
     * The display. 
     */
    private Display display;

    /** 
     * The image streambytes. 
     */
    private byte[] imageStreambytes;

    /**
     * Instantiates a new animation.
     *
     * @param displayComposite the display composite
     * @param imageStreambytes the image streambytes
     */
    public Animation(Composite displayComposite, byte[] imageStreambytes) {
        this.displayComposite = displayComposite;
        this.imageStreambytes = imageStreambytes.clone();
        this.display = displayComposite.getDisplay();
    }

    /**
     * Gets the canvas.
     *
     * @return the canvas
     */
    public Canvas getCanvas() {
        final Canvas canvas = new Canvas(displayComposite, SWT.NULL);
        GridData canvasGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        canvas.setLayoutData(canvasGD);

        ImageLoader imageLoader = new ImageLoader();
        try (InputStream inputStreamReader = new BufferedInputStream(new ByteArrayInputStream(imageStreambytes))) {
            imageLoader.load(inputStreamReader);
        } catch (IOException e) {
            MPPDBIDELoggerUtility.error("failed to load an array of ImageData objects from the specified input stream");
        }
        final ImageData[] imageDatas = imageLoader.data;
        final Image image = new Image(display, imageDatas[0].width, imageDatas[0].height);
        canvas.addPaintListener(new PaintListener() {
            
            /**
             * the paintControl
             * 
             * @param event the event
             */
            public void paintControl(PaintEvent event) {
                event.gc.drawImage(image, 0, 0);
            }
        });
        final GC gc = new GC(image);
        final Thread thread = animationThread(canvas, imageDatas, gc);
        uncaughtExeptionHandle(thread);
        threadInterruptOnCloseListener(thread, canvas);
        thread.start();
        return canvas;
    }

    private Thread animationThread(final Canvas canvas, final ImageData[] imageDatas, final GC gc) {
        final Thread thread = new Thread() {
            int frameIndex = 0;

            /*
             * (non-Javadoc)
             * 
             * @see java.lang.Thread#run()
             */
            public void run() {
                while (!isInterrupted()) {
                    frameIndex %= imageDatas.length;

                    final ImageData frameData = imageDatas[frameIndex];
                    display.asyncExec(new Runnable() {
                        /*
                         * (non-Javadoc)
                         * 
                         * @see java.lang.Runnable#run()
                         */
                        public void run() {
                            Image frame = new Image(display, frameData);
                            gc.drawImage(frame, frameData.x, frameData.y);
                            frame.dispose();
                            if (!canvas.isDisposed()) {
                                canvas.redraw();
                            }
                        }
                    });
                    try {
                        // delay
                        Thread.sleep(imageDatas[frameIndex].delayTime * 10L);
                    } catch (InterruptedException excep) {
                        MPPDBIDELoggerUtility.warn("Warning: InterruptedException occurs so returning");
                        return;
                    }
                    frameIndex += 1;
                }
            }
        };
        thread.setName("Gif_Animation_Thread");
        return thread;
    }

    private void threadInterruptOnCloseListener(final Thread thread, final Canvas canvas) {
        canvas.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent event) {
                thread.interrupt();
            }
        });
    }

    private void uncaughtExeptionHandle(final Thread thread) {
        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            
            /**
             * the uncaughtException
             * 
             * @param thread the thread
             * @param event the event
             */
            public void uncaughtException(Thread thread, Throwable event) {
                MPPDBIDELoggerUtility.error("Failed to show gif image frames");
            }
        });
    }
}
