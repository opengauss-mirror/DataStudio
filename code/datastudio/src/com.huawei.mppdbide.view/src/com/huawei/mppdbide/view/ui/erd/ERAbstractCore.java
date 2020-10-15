/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.erd;

import java.util.Collections;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport.LoggingMode;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;
import com.huawei.mppdbide.presentation.erd.AbstractERPresentation;
import com.huawei.mppdbide.view.erd.contextmenu.ERContextMenu;
import com.huawei.mppdbide.view.erd.convertor.ERModelToGraphModelConvertor;
import com.huawei.mppdbide.view.erd.parts.ERPartFactory;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * The Class ERAbstractCore.
 *
 * @ClassName: ERAbstractCore
 * @Description: The Class ERAbstractCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 * @author: f00512995
 * @version:
 * @since: Sep 28, 2019
 */
public abstract class ERAbstractCore extends AbstractContentPart {

    private IDomain domain;
    private IViewer viewer;
    private FXCanvas canvas;
    private ERContextMenu erContextMenu;
    private AbstractERPresentation presenter;

    /**
     * Creates the part control.
     *
     * @param parent the parent
     */
    public void createPartControl(Composite parent) {
        Injector injector = Guice.createInjector(createModule());

        canvas = new FXCanvas(parent, SWT.BORDER | SWT.READ_ONLY);
        domain = injector.getInstance(IDomain.class);
        viewer = domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));

        /* Get the graph model to set to viewer */
        Graph graph = ERModelToGraphModelConvertor.getGraphModel(presenter);

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

        erContextMenu = new ERContextMenu(viewer, presenter);
        erContextMenu.initERContextMenu();
        viewer.getCanvas().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (me.getButton() == MouseButton.SECONDARY || me.isControlDown()) {
                erContextMenu.show(viewer.getCanvas(), me.getScreenX(), me.getScreenY());
            } else {
                erContextMenu.hide();
            }
        });

    }

    private Module createModule() {
        return new ERModule();
    }

    /**
     * The Class ERModule.
     */
    public static class ERModule extends ZestFxModule {

        /**
         * Bind I content part factory.
         */
        protected void bindIContentPartFactory() {
            binder().bind(IContentPartFactory.class).to(ERPartFactory.class).in(AdaptableScopes.typed(IViewer.class));
        }

        /**
         * Enable adapter map injection.
         */
        @Override
        protected void enableAdapterMapInjection() {
            install(new AdapterInjectionSupport(LoggingMode.PRODUCTION));
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
    }

    /**
     * Sets the presenter.
     *
     * @param presenter the new presenter
     */
    public void setPresenter(AbstractERPresentation presenter) {
        this.presenter = presenter;
    }

    /**
     * Gets the er context menu.
     *
     * @return the er context menu
     */
    public ERContextMenu getErContextMenu() {
        return erContextMenu;
    }

}
