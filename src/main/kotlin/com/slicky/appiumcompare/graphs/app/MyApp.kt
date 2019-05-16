package com.slicky.appiumcompare.graphs.app

import com.slicky.appiumcompare.graphs.view.MainView
import javafx.stage.Stage
import tornadofx.*
import kotlin.system.exitProcess

class MyApp: App(MainView::class, Styles::class) {

    override fun start(stage: Stage) {
        super.start(stage.apply {
            width = 1000.0
            height = 600.0
            dumpStylesheets()
            setOnCloseRequest { exitProcess(0) }
        })
    }

}
