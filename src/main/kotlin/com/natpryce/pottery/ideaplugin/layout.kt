package com.natpryce.pottery.ideaplugin

import java.awt.Component
import java.awt.Container
import java.awt.GridBagConstraints

fun Container.addToGrid(c: Component, block: GridBagConstraints.()->Unit) {
    add(c, GridBagConstraints().apply(block))
}

fun GridBagConstraints.at(gridx: Int, gridy: Int) {
    this.gridx = gridx
    this.gridy = gridy
}

fun GridBagConstraints.stretchX() {
    weightx = 1.0
    weighty = 0.0
    this.fill = GridBagConstraints.HORIZONTAL
}

fun GridBagConstraints.stretch() {
    weightx = 1.0
    weighty = 1.0
    fill = GridBagConstraints.BOTH
}

