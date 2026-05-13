package com.solidhax.apostle.modules.farming

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.api.Island
import com.solidhax.apostle.api.LocationAPI
import com.solidhax.apostle.events.RenderEvent
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.BooleanSetting
import com.solidhax.apostle.ui.setting.ColorSetting
import com.solidhax.apostle.utils.drawTracer
import com.solidhax.apostle.utils.drawWireFrameBox
import com.solidhax.apostle.utils.renderPos
import com.solidhax.apostle.utils.skullTexture
import meteordevelopment.orbit.EventHandler
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.item.Items
import net.minecraft.world.phys.AABB

object PestESP : Module("Pest ESP", "ESP for pests in the garden.", Category.FARMING){

    private val drawBoundingBox by BooleanSetting("Box", "Draw a bounding box at the pests position.")
    private val drawTracer by BooleanSetting("Tracer", "Draw tracer to the pests position.")
    private val color by ColorSetting("Color", "The color of the box and tracer.")

    val pestTextures = mapOf(
        "ewogICJ0aW1lc3RhbXAiIDogMTcyNzkwNDc5NzQ1OSwKICAicHJvZmlsZUlkIiA6ICI0MmIwOTMyZDUwMWI0MWQ1YTM4YjEwOTcxYTYwYmYxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBaXJib2x0MDc4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2YzNzllMDkyNTI4MTczMTRiZDBiNjk0ZjdkNTNiNDhhZjJjN2ZhODQ5OTEwOTgwMmE0MWJiMjk0ZDJmOTNlM2UiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==" to "Field Mouse",
        "ewogICJ0aW1lc3RhbXAiIDogMTY5Njk0NTA2MzI4MSwKICAicHJvZmlsZUlkIiA6ICJjN2FmMWNkNjNiNTE0Y2YzOGY4NWQ2ZDUxNzhjYThlNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJtb25zdGVyZ2FtZXIzMTUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ5MGU3Nzc4MjZhNTI0NjEzNjhlMjZkMWIyZTE5YmZhMWJhNTgyZDYwMjQ4M2U1NDVmNDEyNGQwZjczMTg0MiIKICAgIH0KICB9Cn0=" to "Fly",
        "ewogICJ0aW1lc3RhbXAiIDogMTc3MjE1OTE4MzkzMSwKICAicHJvZmlsZUlkIiA6ICIxNzRjZmRiNGEzY2I0M2I1YmZjZGU0MjRjM2JiMmM2ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJtYXJhZWwxOCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iZWU0ZmNjNWFhYzI3YmRiYjBiZTIxMGRmMDhiMDViY2E1YWViYzg5YmYyODIxZjYwOGE1NWZkMmNmMDQzNGJlIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=" to "Lunar Moth",
        "ewogICJ0aW1lc3RhbXAiIDogMTcyMzE3OTgxMTI2NCwKICAicHJvZmlsZUlkIiA6ICJjZjc4YzFkZjE3ZTI0Y2Q5YTIxYmU4NWQ0NDk5ZWE4ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXR0c0FybW9yU3RhbmRzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2EyNGM2OWY5NmNlNTU2MjIxZTE5NWM4ZWYyYmZhZDcxZWJmN2Y5NWY1YWU5MTRhNDg0YThkMGVjMjE2NzI2NzQiCiAgICB9CiAgfQp9=" to "Cricket",
        "ewogICJ0aW1lc3RhbXAiIDogMTY5NzU1NzA3NzAzNywKICAicHJvZmlsZUlkIiA6ICI0YjJlMGM1ODliZjU0ZTk1OWM1ZmJlMzg5MjQ1MzQzZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJfTmVvdHJvbl8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGIyNGE0ODJhMzJkYjFlYTc4ZmI5ODA2MGIwYzJmYTRhMzczY2JkMThhNjhlZGRkZWI3NDE5NDU1YTU5Y2RhOSIKICAgIH0KICB9Cn0=" to "Locust",
        "ewogICJ0aW1lc3RhbXAiIDogMTYxODQxOTcwMTc1MywKICAicHJvZmlsZUlkIiA6ICI3MzgyZGRmYmU0ODU0NTVjODI1ZjkwMGY4OGZkMzJmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCdUlJZXQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYThhYmI0NzFkYjBhYjc4NzAzMDExOTc5ZGM4YjQwNzk4YTk0MWYzYTRkZWMzZWM2MWNiZWVjMmFmOGNmZmU4IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=" to "Rat",
        "ewogICJ0aW1lc3RhbXAiIDogMTY5Njk0NTAyOTQ2MSwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTJhOWZlMDViYzY2M2VmY2QxMmU1NmEzY2NjNWVjMDM1YmY1NzdiNzg3MDg1NDhiNmY0ZmZjZjFkMzBlY2NmZSIKICAgIH0KICB9Cn0=" to "Mosquito",
        "ewogICJ0aW1lc3RhbXAiIDogMTY5NzQ3MDQ1OTc0NywKICAicHJvZmlsZUlkIiA6ICIyNTBlNzc5MjZkNDM0ZDIyYWM2MTQ4N2EyY2M3YzAwNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJMdW5hMTIxMDUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQwM2JhNDAyN2EzMzNkOGQyZmQzMmFiNTlkMWNmZGJhYTdkOTA4ZDgwZDIzODFkYjJhNjljYmU2NTQ1MGFkOCIKICAgIH0KICB9Cn0=" to "Earthworm",
        "ewogICJ0aW1lc3RhbXAiIDogMTY5Njg3MDQxOTcyNSwKICAicHJvZmlsZUlkIiA6ICJkYjYzNWE3MWI4N2U0MzQ5YThhYTgwOTMwOWFhODA3NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJFbmdlbHMxNzQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmU2YmFmNjQzMWE5ZGFhMmNhNjA0ZDVhM2MyNmU5YTc2MWQ1OTUyZjA4MTcxNzRhNGZlMGI3NjQ2MTZlMjFmZiIKICAgIH0KICB9Cn0=" to "Mite",
        "ewogICJ0aW1lc3RhbXAiIDogMTY5Njg3MDQwNTk1NCwKICAicHJvZmlsZUlkIiA6ICJiMTUyZDlhZTE1MTM0OWNmOWM2NmI0Y2RjMTA5NTZjOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaXNxdW90aCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82NTQ4NWM0YjM0ZTViNTQ3MGJlOTRkZTEwMGU2MWY3ODE2ZjgxYmM1YTExZGZkZjBlY2NmODkwMTcyZGE1ZDBhIgogICAgfQogIH0KfQ==" to "Moth",
        "ewogICJ0aW1lc3RhbXAiIDogMTY5NzQ3MDQ0MzA4MiwKICAicHJvZmlsZUlkIiA6ICJkOGNkMTNjZGRmNGU0Y2IzODJmYWZiYWIwOGIyNzQ4OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJaYWNoeVphY2giLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E3OWQwZmQ2NzdiNTQ1MzA5NjExMTdlZjg0YWRjMjA2ZTJjYzUwNDVjMTM0NGQ2MWQ3NzZiZjhhYzJmZTFiYSIKICAgIH0KICB9Cn0=" to "Slug",
        "ewogICJ0aW1lc3RhbXAiIDogMTcyMzE3OTc4OTkzNCwKICAicHJvZmlsZUlkIiA6ICJlMjc5NjliODYyNWY0NDg1YjkyNmM5NTBhMDljMWMwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLRVZJTktFTE9LRSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MGExZTgzNmJmMTk2OGIyZWFhNDgzNzIyN2ExOTIwNGYxNzI5NWQ4NzBlZTllNzU0YmQ2YjZkNjBkZGJlZDNjIgogICAgfQogIH0KfQ==" to "Beetle",
        "ewogICJ0aW1lc3RhbXAiIDogMTc2MDQ1MDQyMjEzNiwKICAicHJvZmlsZUlkIiA6ICIzNDY4Y2VjMWFlOTY0YWRmYWQyNjEzMGEwZGQ0NjRkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzdXJlZWxta18iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwi IDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNlNzllOTBhZGYzNDcxOGYzMTNlYzI0ZDZjNjEzNWI2OWIzNzg4YzYxODQ5ODQ0NmNjYzgzY2E2NDBjMGIxNCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9" to "Firefly",
        "ewogICJ0aW1lc3RhbXAiIDogMTc2MDQ1MDQxODQzNywKICAicHJvZmlsZUlkIiA6ICIwNjY5Y2E1MGYyZWU0NTQxODhlYWQ3YTM3NTkzNDRlMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDcjR6eWNsb3duVFYiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwi IDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjU0YWZmNGMwYjJkY2UzYTY3MjM0OWNjMGVlOWU2ZjNhOWRlZWJlNGIzNTU2ZTg0NjExZWNhMjUwYTc4MjFiZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9" to "Dragonfly",
        "ewogICJ0aW1lc3RhbXAiIDogMTc2MDQ1MDQxOTYxMiwKICAicHJvZmlsZUlkIiA6ICI0OWIzODUyNDdhMWY0NTM3YjBmN2MwZTFmMTVjMTc2NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJiY2QyMDMzYzYzZWM0YmY4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMi IDogewogICAgIlNLSU4i IDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFlMDRiYjYzNjdjYWE0ZTg4ZjVmZDBlZTgwZjA3NDVkMTM3YTYwNjAyMjNkYmJjNDJhMTY0NzFmZGY2NGJiODMiLAogICAgICAibWV0YWRhdGEi IDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==" to "Praying Mantis"
    )

    init {
        @EventHandler
        fun onDraw(event: RenderEvent.Extract) {
            if(!enabled || !LocationAPI.isCurrentArea(Island.Garden)) return

            mc.level?.entitiesForRendering()?.forEach { e ->
                if(!e.isAlive || e !is ArmorStand) return@forEach

                val entityHelmetSlot = e.getItemBySlot(EquipmentSlot.HEAD)
                if(entityHelmetSlot.isEmpty || entityHelmetSlot.item != Items.PLAYER_HEAD) return@forEach

                val skullTexture = entityHelmetSlot.skullTexture ?: return@forEach
                if(skullTexture !in pestTextures) return@forEach

                val pestName = pestTextures[skullTexture] ?: return@forEach

                val renderPosition = e.renderPos
                val xOffset = 0.5
                val yOffset = if (pestName == "Earthworm") 0.5 else 1.3
                val zOffset = 0.5

                val bounds = AABB(
                    renderPosition.x - xOffset,
                    renderPosition.y + yOffset,
                    renderPosition.z - zOffset,

                    renderPosition.x - xOffset + 1.0,
                    renderPosition.y + yOffset + 1.0,
                    renderPosition.z - zOffset + 1.0
                )

                if(drawBoundingBox) event.drawWireFrameBox(bounds, color)
                if(drawTracer) event.drawTracer(bounds.center, color)
            }
        }
    }

}