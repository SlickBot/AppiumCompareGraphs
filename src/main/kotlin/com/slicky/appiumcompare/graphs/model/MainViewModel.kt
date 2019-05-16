package com.slicky.appiumcompare.graphs.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class MainViewData {

    val inputPathProperty = SimpleStringProperty("")
    val outputPathProperty = SimpleStringProperty("")

    val projectTakeCountProperty = SimpleIntegerProperty(5)
    val projectJavaActiveProperty = SimpleBooleanProperty(true)
    val projectKotlinActiveProperty = SimpleBooleanProperty(false)
    val projectAnkoActiveProperty = SimpleBooleanProperty(false)
    val projectCpuActiveProperty = SimpleBooleanProperty(true)
    val projectMemActiveProperty = SimpleBooleanProperty(false)

    val averageTakeCountProperty = SimpleIntegerProperty(1000)
    val averageJavaActiveProperty = SimpleBooleanProperty(true)
    val averageKotlinActiveProperty = SimpleBooleanProperty(true)
    val averageAnkoActiveProperty = SimpleBooleanProperty(false)
    val averageCpuActiveProperty = SimpleBooleanProperty(true)
    val averageMemActiveProperty = SimpleBooleanProperty(false)

}

class MainViewModel(private val model: MainViewData) : ViewModel() {

    val inputPath = bind { model.inputPathProperty }
    val outputPath = bind { model.outputPathProperty }

    val projectTakeCount = bind { model.projectTakeCountProperty }
    val projectJavaActive = bind { model.projectJavaActiveProperty }
    val projectKotlinActive = bind { model.projectKotlinActiveProperty }
    val projectAnkoActive = bind { model.projectAnkoActiveProperty }
    val projectCpuActive = bind { model.projectCpuActiveProperty }
    val projectMemActive = bind { model.projectMemActiveProperty }

    val averageTakeCount = bind { model.averageTakeCountProperty }
    val averageJavaActive = bind { model.averageJavaActiveProperty }
    val averageKotlinActive = bind { model.averageKotlinActiveProperty }
    val averageAnkoActive = bind { model.averageAnkoActiveProperty }
    val averageCpuActive = bind { model.averageCpuActiveProperty }
    val averageMemActive = bind { model.averageMemActiveProperty }


}
