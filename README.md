# ExpandableGroup

类似于ExpandListView，每一个item有一个title和content，但是不同于ExpandListView的是当第一项是当前项的时候全部item均会展示在屏幕上（当前项展示title与content，其余项只展示title）

![demo.gif](https://github.com/EndSmile/ExpandableGroup/blob/master/img/demo.gif)

### 功能
1. 滑动和点击切换item
2. 动画开始结束事件回调
3. itemTitle在切换的过程中高度自动变化
4. 动态计算每一项content的高度
5. 在边缘处不能滑动时添加Android原生的阴影效果
