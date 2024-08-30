package com.mj.core.common.compose


import android.text.Layout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.mj.core.priceDecimalFormat
import com.mj.core.theme.green_400
import com.mj.core.theme.green_500
import com.mj.core.theme.white
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShadow
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.markerCornered
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.Insets
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarkerValueFormatter
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LineChart(modifier: Modifier, numbers: List<Number>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                lineSeries {
                    series(y = numbers)
                }
            }
        }
    }
    LineChartContent(modifier, modelProducer)
}

@Composable
private fun LineChartContent(modifier: Modifier, modelProducer: CartesianChartModelProducer) {
    CartesianChartHost(
        modifier = modifier,
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    rememberLine(
                        fill = remember { LineCartesianLayer.LineFill.single(fill(green_400)) },
                    )
                )
            ),
            startAxis = rememberStartAxis(
                label = rememberStartAxisLabel(),
                valueFormatter = startValueFormatter,
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
            ),
            bottomAxis = rememberBottomAxis(
                label = rememberBottomAxisLabel(),
                guideline = null,
                valueFormatter = bottomValueFormatter
            ),
            marker = rememberMarker(),
        ),
        modelProducer = modelProducer,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}

@Composable
private fun rememberStartAxisLabel() =
    rememberAxisLabelComponent(
        color = white,
        padding = Dimensions.of(4.dp, 2.dp),
        background = rememberShapeComponent(green_500, Shape.rounded(4.dp)),
    )

@Composable
private fun rememberBottomAxisLabel() =
    rememberAxisLabelComponent(color = Color.Black)

@Composable
internal fun rememberMarker(
    labelPosition: DefaultCartesianMarker.LabelPosition = DefaultCartesianMarker.LabelPosition.Top,
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackgroundShape = Shape.markerCornered(Corner.FullyRounded)
    val labelBackground = rememberShapeComponent(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = labelBackgroundShape,
        shadow =
        rememberShadow(
            radius = LABEL_BACKGROUND_SHADOW_RADIUS_DP.dp,
            dy = LABEL_BACKGROUND_SHADOW_DY_DP.dp,
        ),
    )
    val label = rememberTextComponent(
        color = MaterialTheme.colorScheme.onSurface,
        textAlignment = Layout.Alignment.ALIGN_CENTER,
        padding = Dimensions.of(4.dp, 4.dp),
        background = labelBackground,
        minWidth = TextComponent.MinWidth.fixed(40.dp),
    )
    val indicatorFrontComponent = rememberShapeComponent(MaterialTheme.colorScheme.surface, Shape.Pill)
    val indicatorCenterComponent = rememberShapeComponent(shape = Shape.Pill)
    val indicatorRearComponent = rememberShapeComponent(shape = Shape.Pill)
    val indicator = rememberLayeredComponent(
        rear = indicatorRearComponent,
        front =
        rememberLayeredComponent(
            rear = indicatorCenterComponent,
            front = indicatorFrontComponent,
            padding = Dimensions.of(5.dp),
        ),
        padding = Dimensions.of(10.dp),
    )
    val guideline = rememberAxisGuidelineComponent()
    return remember(label, labelPosition, indicator, showIndicator, guideline) {
        object : DefaultCartesianMarker(
            label = label,
            labelPosition = labelPosition,
            valueFormatter = markerValueFormatter,
            indicator =
            if (showIndicator) {
                { color ->
                    LayeredComponent(
                        rear = ShapeComponent(green_400.toArgb(), Shape.Pill),
                        front =
                        LayeredComponent(
                            rear =
                            ShapeComponent(
                                color = color,
                                shape = Shape.Pill,
                                shadow = Shadow(radiusDp = 12f, color = color),
                            ),
                            front = indicatorFrontComponent,
                            padding = Dimensions.of(5.dp),
                        ),
                        padding = Dimensions.of(10.dp),
                    )
                }
            } else {
                null
            },
            indicatorSizeDp = 12f,
        ) {
            override fun updateInsets(
                context: CartesianMeasuringContext,
                horizontalDimensions: HorizontalDimensions,
                model: CartesianChartModel,
                insets: Insets,
            ) {
                with(context) {
                    val baseShadowInsetDp = CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP
                    var topInset = (baseShadowInsetDp - LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    var bottomInset = (baseShadowInsetDp + LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    when (labelPosition) {
                        LabelPosition.Top,
                        LabelPosition.AbovePoint -> topInset += label.getHeight(context) + tickSizeDp.pixels

                        LabelPosition.Bottom -> bottomInset += label.getHeight(context) + tickSizeDp.pixels
                        LabelPosition.AroundPoint -> {}
                    }
                    insets.ensureValuesAtLeast(top = topInset, bottom = bottomInset)
                }
            }
        }
    }
}

private const val LABEL_BACKGROUND_SHADOW_RADIUS_DP = 4f
private const val LABEL_BACKGROUND_SHADOW_DY_DP = 2f
private const val CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER = 1.4f

private val startValueFormatter = CartesianValueFormatter.decimal(priceDecimalFormat)
private val bottomValueFormatter = CartesianValueFormatter { value, values, _ ->
    when (val day = (values.maxX.toInt() - value.toInt())) {
        0 -> "오늘"
        1 -> "어제"
        else -> "${day}일 전"
    }
}
private val markerValueFormatter = DefaultCartesianMarkerValueFormatter(
    decimalFormat = (priceDecimalFormat)
)