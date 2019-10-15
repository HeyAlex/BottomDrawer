<h1 align="center">[WIP]Bottom Drawer</h1>
<p align="center">An easy way to provide bottom sheets with animation</p>

### Summary
This library provide modal bottom sheet with animations.

![](/raw/sample_google_task.gif)

## Sample
![](/raw/sample_custom_1.gif)
![](/raw/sample_custom_2.gif)

## Usage

Example:

```kotlin
class GoogleTaskExampleDialog : BottomDrawerFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.your_layout, container, false)
    }
}
```
