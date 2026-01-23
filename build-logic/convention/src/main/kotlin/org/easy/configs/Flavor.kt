package org.easy.configs

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

enum class FlavorDimension {
    Environment
}

enum class Flavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    staging(FlavorDimension.Environment, ".staging"),
    prod(FlavorDimension.Environment)
}

fun configureFlavors(
    commonExtension: CommonExtension,
    flavorConfigurationBlock: ProductFlavor.(flavor: Flavor) -> Unit = {}
) {
    commonExtension.apply {
        flavorDimensions += FlavorDimension.Environment.name
        Flavor.values().forEach { flavor ->
            productFlavors.create(flavor.name) {
                dimension = flavor.dimension.name
                flavorConfigurationBlock(this, flavor)
                if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                    flavor.applicationIdSuffix?.let { applicationIdSuffix = it }
                }
            }
        }
    }
}