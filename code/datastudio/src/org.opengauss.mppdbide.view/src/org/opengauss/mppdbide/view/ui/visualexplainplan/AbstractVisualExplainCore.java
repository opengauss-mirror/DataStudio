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

package org.opengauss.mppdbide.view.ui.visualexplainplan;

import java.util.Collections;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport.LoggingMode;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.SetMultimap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.ui.visualexplainplan.parts.CustomContentPartFactory;
import org.opengauss.mppdbide.view.ui.visualexplainplan.parts.DSGraphLayoutBehavior;
import org.opengauss.mppdbide.view.ui.visualexplainplan.parts.OpenNodePropertyOnClickHandler;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractVisualExplainCore.
 *
 * @since 3.0.0
 */
public abstract class AbstractVisualExplainCore extends AbstractContentPart {

    /**
     * Gets the event broker.
     *
     * @return the event broker
     */
    protected abstract IEventBroker getEventBroker();

    /**
     * The Constant ID.
     */
    public static final String VIEW_ID = "graphicalexplainplan.view";

    /**
     * the <code>EditDomain</code>
     */
    private IDomain domain;

    private IViewer viewer;

    private VisualExplainPlanUIPresentation vepPresenter;

    private FXCanvas canvas;

    /**
     * Instantiates a new abstract visual explain core.
     */
    protected AbstractVisualExplainCore() {
        this.vepPresenter = null;
    }

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param uiPresenatationWrapper the ui presenatation wrapper
     */
    public void createPartControl(Composite parent, VisualExplainPlanUIPresentation uiPresenatationWrapper) {

        this.vepPresenter = uiPresenatationWrapper;
        createViewer(parent);
    }

    private void createViewer(Composite parent) {

        Injector injector = Guice.createInjector(createModule(vepPresenter.getExplainPlanTabId()));

        canvas = new FXCanvas(parent, SWT.BORDER | SWT.READ_ONLY);
        domain = injector.getInstance(IDomain.class);
        viewer = domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));

        /* Get the graph model to set to viewer */
        Graph graph = vepPresenter.getPresenter().getGraphModel();

        /* Using Platform.runLater() for UI thread modifications of FX */
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Scene scene = new Scene(viewer.getCanvas());
                canvas.setScene(scene);
                viewer.getContents().setAll(Collections.singletonList(graph));
                /* activate domain only after viewers have been hooked */
                domain.activate();
            }
        });

        /* Reset view to center once rendered */
        ViewResetJob viewResetJob = new ViewResetJob();
        viewResetJob.schedule();
    }

    private Module createModule(String tabId) {
        return new CustomModule(tabId);
    }

    /**
     * The Class CustomModule.
     */
    public class CustomModule extends ZestFxModule {

        private String tabId;

        public CustomModule(String tabId) {
            super();
            this.tabId = tabId;
        }

        /**
         * Bind I content part factory.
         */
        protected void bindIContentPartFactory() {
            binder().bind(IContentPartFactory.class).to(CustomContentPartFactory.class)
                    .in(AdaptableScopes.typed(IViewer.class));
        }

        /**
         * Enable adapter map injection.
         */
        @Override
        protected void enableAdapterMapInjection() {
            install(new AdapterInjectionSupport(LoggingMode.PRODUCTION));
        }

        /**
         * Bind node part adapters.
         *
         * @param adapterMapBinder the adapter map binder
         */
        @Override
        protected void bindNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
            super.bindNodePartAdapters(adapterMapBinder);
            adapterMapBinder.addBinding(AdapterKey.defaultRole())
                    .toInstance(new OpenNodePropertyOnClickHandler(getEventBroker(), tabId));
        }

        /**
         * Bind hover handle part factory as content viewer adapter.
         *
         * @param adapterMapBinder the adapter map binder
         */
        @Override
        protected void
                bindHoverHandlePartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
            /* Do not use super method as we don't need Node collapse options */
        }

        /**
         * Bind graph part adapters.
         *
         * @param adapterMapBinder the adapter map binder
         */
        @Override
        protected void bindGraphPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
            adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(LayoutContext.class);
            adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DSGraphLayoutBehavior.class);
        }
    }

    /**
     * Do get content anchorages.
     *
     * @return the sets the multimap
     */
    @Override
    protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
        return null;
    }

    /**
     * Do get content children.
     *
     * @return the list
     */
    @Override
    protected List<? extends Object> doGetContentChildren() {
        return null;
    }

    /**
     * Do create visual.
     *
     * @return the node
     */
    @Override
    protected Node doCreateVisual() {
        return null;
    }

    /**
     * Do refresh visual.
     *
     * @param visual the visual
     */
    @Override
    protected void doRefreshVisual(Node visual) {
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        this.viewer = null;
        this.domain = null;
        this.canvas = null;
        this.vepPresenter = null;

    }

    /**
     * Handle reset view port.
     */
    protected void handleResetViewPort() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (viewer.getRootPart() != null) {
                        ViewportPolicy viewportPolicy = viewer.getRootPart().getAdapter(ViewportPolicy.class);
                        viewportPolicy.init();
                        viewportPolicy.fitToSize(1, 1);
                        ITransactionalOperation commit = viewportPolicy.commit();
                        viewer.getDomain().execute(commit, null);
                    }
                } catch (ExecutionException | IllegalStateException exception) {
                    MPPDBIDELoggerUtility.error("Failed to reset zoom level for VEP");
                }
            }
        });

    }

    private class ViewResetJob extends Job {

        public ViewResetJob() {
            super("VEP: View Reset Job");
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                Thread.sleep(2000);
                handleResetViewPort();
            } catch (InterruptedException exception) {
                MPPDBIDELoggerUtility.error("Visual Explain Plan View reset job interrupted", exception);
            }
            return Status.OK_STATUS;
        }

    }
}
