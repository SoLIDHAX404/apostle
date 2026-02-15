package org.solidhax.apostle.config.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.universal.USound
import gg.essential.vigilance.gui.VigilancePalette
import gg.essential.vigilance.utils.onLeftClick
import java.awt.Color

class MultiSelectDropdown(
    private val label: String,
    private val options: List<String>,
    initiallySelected: Set<String> = emptySet(),
    private val onChange: (Set<String>) -> Unit = {}
) : UIContainer() {

    private val selected = initiallySelected.toMutableSet()
    private var expanded = false

    private val rowHeight = 14f
    private val maxListHeight = 90f

    private val bgNormal = VigilancePalette.getButton()
    private val bgHover = VigilancePalette.getButtonHighlight()
    private val textNormal = VigilancePalette.getText()
    private val textSelected = VigilancePalette.getTextActive()

    private var root: UIComponent? = null
    private var rootClickHooked = false

    private val header = UIBlock(bgNormal).constrain {
        x = 0.pixels(); y = 0.pixels()
        width = RelativeConstraint(1f); height = RelativeConstraint(1f)
    } childOf this

    private val headerText = UIText("").constrain {
        x = 6.pixels(); y = CenterConstraint()
        textScale = 1.pixels(); color = textNormal.toConstraint()
    } childOf header

    private val chevron = UIText("▾").constrain {
        x = 6.pixels(true); y = CenterConstraint()
        color = textNormal.toConstraint()
    } childOf header

    private val listHolder = UIBlock(bgNormal).constrain {
        x = 0.pixels(); y = SiblingConstraint(2f)
        width = RelativeConstraint(1f); height = 0.pixels()
    } effect ScissorEffect()

    private val scroll = ScrollComponent(innerPadding = 0f).constrain {
        x = 0.pixels(); y = 0.pixels()
        width = RelativeConstraint(1f); height = RelativeConstraint(1f)
    } childOf listHolder

    private val content = UIContainer().constrain {
        x = 0.pixels(); y = 0.pixels()
        width = RelativeConstraint(1f); height = ChildBasedSizeConstraint()
    } childOf scroll

    init {
        listHolder childOf this
        rebuildRows(); refreshHeader()

        header.onMouseEnter { header.setColor(bgHover) }
            .onMouseLeave { header.setColor(bgNormal) }
            .onLeftClick { USound.playButtonPress(); setExpanded(!expanded) }
    }

    fun attachToRoot(root: UIComponent) {
        this.root = root
        if (rootClickHooked) return
        rootClickHooked = true

        root.onMouseClick {
            if (!expanded) return@onMouseClick
            val x = it.absoluteX.toDouble()
            val y = it.absoluteY.toDouble()
            if (!isPointInside(header, x, y) && !isPointInside(listHolder, x, y)) setExpanded(false)
        }
    }

    fun getSelected(): Set<String> = selected.toSet()

    private fun setExpanded(value: Boolean) {
        if (expanded == value) return
        expanded = value
        chevron.setText(if (expanded) "▴" else "▾")

        if (!expanded) {
            listHolder.setHeight(0.pixels())
            if (listHolder.parent != this) {
                listHolder.parent.removeChild(listHolder)
                listHolder childOf this
                listHolder.constrain { x = 0.pixels(); y = SiblingConstraint(2f); width = RelativeConstraint(1f) }
            }
            return
        }

        val r = root ?: run {
            listHolder.setHeight((options.size * rowHeight).coerceAtMost(maxListHeight).pixels())
            return
        }

        if (listHolder.parent != r) {
            listHolder.parent.removeChild(listHolder)
            listHolder childOf r
        }

        val left = header.getLeft()
        val top = header.getTop()
        val widthPx = header.getWidth()
        val dropdownHeightPx = getHeight()
        val desiredHeight = (options.size * rowHeight).coerceAtMost(maxListHeight)

        listHolder.constrain { x = left.pixels(); y = (top + dropdownHeightPx).pixels(); width = widthPx.pixels() }
        listHolder.setHeight(desiredHeight.pixels())
    }

    private fun refreshHeader() {
        val summary = when (selected.size) {
            0 -> "None"
            1 -> selected.first()
            else -> "${selected.size} selected"
        }
        headerText.setText("$label: $summary")
    }

    private fun rebuildRows() {
        content.clearChildren()
        options.forEach { opt ->
            val row = UIBlock(Color(0, 0, 0, 0)).constrain {
                x = 0.pixels(); y = SiblingConstraint(0f)
                width = RelativeConstraint(1f); height = rowHeight.pixels()
            } childOf content

            val text = UIText(opt).constrain {
                x = 8.pixels(); y = CenterConstraint()
                color = if (selected.contains(opt)) textSelected.toConstraint() else textNormal.toConstraint()
            } childOf row

            row.onMouseEnter { row.setColor(bgHover) }
                .onMouseLeave { row.setColor(Color(0, 0, 0, 0)) }
                .onLeftClick {
                    USound.playButtonPress()
                    if (selected.contains(opt)) selected.remove(opt) else selected.add(opt)
                    text.constrain { color = if (selected.contains(opt)) textSelected.toConstraint() else textNormal.toConstraint() }
                    refreshHeader()
                    onChange(getSelected())
                }
        }
    }

    private fun isPointInside(component: UIComponent, mouseX: Double, mouseY: Double): Boolean {
        val x1 = component.getLeft().toDouble()
        val y1 = component.getTop().toDouble()
        val x2 = x1 + component.getWidth().toDouble()
        val y2 = y1 + component.getHeight().toDouble()
        return mouseX in x1..x2 && mouseY in y1..y2
    }
}