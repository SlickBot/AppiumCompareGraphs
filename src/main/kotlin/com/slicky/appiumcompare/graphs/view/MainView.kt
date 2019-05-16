package com.slicky.appiumcompare.graphs.view

import com.slicky.appiumcompare.graphs.app.Styles
import com.slicky.appiumcompare.graphs.ctrl.MainController
import javafx.application.Platform
import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.BorderPane
import tornadofx.*

class MainView : View("AppiumCompare GRAPHS") {

    private val ctrl: MainController by inject()

    lateinit var mainPane: BorderPane
    lateinit var graph: XYChart<String, Number>
    lateinit var indicator: ProgressIndicator

    val xAxis = CategoryAxis().apply { label = "Time" }
    val yAxis = NumberAxis(0.0, 120.0, 1.0)

    var isLoading: Boolean = false
        set(value) {
            field = value
            Platform.runLater {
                mainPane.isDisable = value
                indicator.isVisible = value
            }
        }


    override val root = borderpane {
        mainPane = this

        center = stackpane {
            graph = areachart(x = xAxis, y = yAxis) {
                addClass(Styles.graph)
                createSymbols = false
            }
            indicator = progressindicator {
                addClass(Styles.indicator)
                isVisible = false
            }
        }

        right = form {
            prefWidth = 325.0

            fieldset("Paths") {
                field("Input path") { textfield(ctrl.model.inputPath).required() }
//                field("Output path") { textfield(ctrl.model.outputPath).required() }
            }

            fieldset("Project graphs") {
                field("Take") { integerfield(ctrl.model.projectTakeCount).required() }
                field("Project") {
                    togglegroup {
                        radiobutton("Java", ctrl.model.projectJavaActive)
                        radiobutton("Kotlin", ctrl.model.projectKotlinActive)
//                        radiobutton("Anko", ctrl.model.projectAnkoActive)
                    }
                }
                field("Type") {
                    togglegroup {
                        radiobutton("CPU", ctrl.model.projectCpuActive)
                        radiobutton("Memory", ctrl.model.projectMemActive)
                    }
                }
                field {
                    hbox {
                        addClass(Styles.buttonField)
                        button("Show project data") { setOnAction { ctrl.refreshProjectData() } }
                    }
                }
            }

            fieldset("Average graphs") {
                field("Take") { integerfield(ctrl.model.averageTakeCount).required() }
                field("Projects") {
                    radiobutton("Java", ctrl.model.averageJavaActive)
                    radiobutton("Kotlin", ctrl.model.averageKotlinActive)
//                    radiobutton("Anko", ctrl.model.averageAnkoActive)
                }
                field("Type") {
                    togglegroup {
                        radiobutton("CPU", ctrl.model.averageCpuActive)
                        radiobutton("Memory", ctrl.model.averageMemActive)
                    }
                }
                field {
                    hbox {
                        addClass(Styles.buttonField)
                        button("Show average data") { setOnAction { ctrl.refreshAverageData() } }
                    }
                }
            }

            fieldset {
                addClass(Styles.buttonField)
                button("Save graph") { setOnAction { ctrl.saveSnapshot() } }
            }

        }
    }

    private fun EventTarget.radiobutton(
            text: String? = null,
            property: Property<Boolean>? = null,
            group: ToggleGroup? = getToggleGroup(),
            op: RadioButton.() -> Unit = {}
    ): RadioButton = radiobutton(text, group) {
        if (property != null) selectedProperty().bindBidirectional(property)
        op()
    }

}
