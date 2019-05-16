package com.slicky.appiumcompare.graphs.ctrl

import com.slicky.appiumcompare.graphs.model.MainViewData
import com.slicky.appiumcompare.graphs.model.MainViewModel
import com.slicky.appiumcompare.graphs.view.MainView
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.SnapshotParameters
import javafx.scene.chart.XYChart
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import javax.imageio.ImageIO

class MainController : Controller() {

    private val view: MainView by inject()

    val model = MainViewModel(MainViewData())

    init {
        initInputPath()
//        initOutputPath()
    }

    fun saveSnapshot() {
        chooseFile("Save Snapshot",
                arrayOf(FileChooser.ExtensionFilter("PNG Images", "*.png")),
                FileChooserMode.Save,
                primaryStage
        ).firstOrNull()?.let { saveImageToFile(it) }
    }

    fun refreshProjectData() {

        val inputPath = model.inputPath.value
        val inputDir = File(inputPath)
        if (!inputDir.exists()) {
            println("Input Dir does not exist!")
            return
        }

        val projects = mutableListOf<String>()
        if (model.projectJavaActive.value) projects += "JavaFakeSocial"
        if (model.projectKotlinActive.value) projects += "KotlinFakeSocial"
        if (model.projectAnkoActive.value) projects += "AnkoFakeSocial"

        val type = when {
            model.projectCpuActive.value -> {
                view.yAxis.label = "CPU usage"
                view.yAxis.upperBound = 120.0
                "cpu"
            }
            model.projectMemActive.value -> {
                view.yAxis.label = "Memory usage"
                view.yAxis.upperBound = 10.0
                "mem"
            }
            else -> throw IllegalStateException()
        }

        val takeCount = model.projectTakeCount.value

        runRefreshProjectGraph(inputDir, projects, type, takeCount.toInt())
    }

    private fun runRefreshProjectGraph(inputDir: File, projects: List<String>, type: String, takeCount: Int) {
        runAsync {
            view.isLoading = true
            Platform.runLater { view.graph.data.clear() }
            inputDir.listFiles { _, s ->
                projects.any { project ->
                    s.startsWith("$project-$type")
                }
            }.map { file ->
                file.readText()
            }.map { line ->
                line.split(",").map { value ->
                    value.toDouble()
                }
            }.filter { values ->
                values.none { it == -1.0 }
            }.shuffled().take(takeCount).forEachIndexed { seriesIdx, values ->
                val series = createNewSeries((seriesIdx + 1).toString())
                series.data.addAll(values.mapIndexed { valIdx, value ->
                    createNewData((valIdx + 1).toString(), value)
                })
                Platform.runLater { view.graph.data.add(series) }
            }
            view.isLoading = false
        }
    }

    fun refreshAverageData() {

        val inputPath = model.inputPath.value
        val inputDir = File(inputPath)
        if (!inputDir.exists()) {
            println("Input Dir does not exist!")
            return
        }

        val projects = mutableListOf<String>()
        if (model.averageJavaActive.value) projects += "JavaFakeSocial"
        if (model.averageKotlinActive.value) projects += "KotlinFakeSocial"
        if (model.averageAnkoActive.value) projects += "AnkoFakeSocial"

        val type = when {
            model.averageCpuActive.value -> {
                view.yAxis.label = "CPU usage"
                view.yAxis.upperBound = 100.0
                "cpu"
            }
            model.averageMemActive.value -> {
                view.yAxis.label = "Memory usage"
                view.yAxis.upperBound = 6.0
                "mem"
            }
            else -> throw IllegalStateException()
        }

        val takeCount = model.averageTakeCount.value

        runRefreshAverageGraphs(inputDir, projects, type, takeCount.toInt())
    }

    private fun runRefreshAverageGraphs(inputDir: File, projects: List<String>, type: String, takeCount: Int) {
        runAsync {
            view.isLoading = true
            Platform.runLater { view.graph.data.clear() }

            projects.forEach { project ->
                val series = createNewSeries(project)

                val list = inputDir.listFiles { _, s ->
                    s.startsWith("$project-$type")
                }.map { file ->
                    file.readText()
                }.map { line ->
                    line.split(",").map { value ->
                        value.toDouble()
                    }
                }.filter { values ->
                    values.none { it == -1.0 }
                }.shuffled().take(takeCount)

                val maxSize = list.maxBy { it.size }?.size ?: 0
                val avgValues = mutableListOf<Double>()
                for (i in 0 until maxSize) {
                    var counter = 0
                    var sum = 0.0
                    for (values in list) {
                        val value = values.getOrNull(i)
                        if (value != null) {
                            counter += 1
                            sum += value
                        }
                    }
                    avgValues.add(i, if (counter != 0) sum / counter else 0.0)
                }

                series.data.addAll(avgValues.mapIndexed { valIdx, value ->
                    createNewData((valIdx + 1).toString(), value)
                })
                Platform.runLater {
                    view.graph.data.add(series)
                }
            }
            view.isLoading = false
        }
    }

    private fun initInputPath() {
        preferences { model.inputPath.value = get("input_path", "") }
        model.inputPath.onChange { preferences { put("input_path", it) } }
    }

//    private fun initOutputPath() {
//        preferences { model.outputPath.value = get("output_path", "") }
//        model.outputPath.onChange { preferences { put("output_path", it) } }
//    }

    private fun saveImageToFile(file: File) {
        Platform.runLater {
            val image = view.graph.snapshot(SnapshotParameters(), null)
            runAsync {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file)
            }
        }
    }

    private fun createNewSeries(title: String) =
            XYChart.Series(title, mutableListOf<XYChart.Data<String, Number>>().observable())

    private fun createNewData(label: String, value: Number) =
            XYChart.Data<String, Number>(label, value)

}
