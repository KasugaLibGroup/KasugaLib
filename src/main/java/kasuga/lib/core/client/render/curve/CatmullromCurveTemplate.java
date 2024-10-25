package kasuga.lib.core.client.render.curve;

import com.google.common.collect.Lists;
import kasuga.lib.core.client.model.anim_json.CatmullRomUtils;
import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.util.data_type.Pair;

import java.util.ArrayList;
import java.util.List;

public class CatmullromCurveTemplate implements CurveTemplate {

    private final List<Pair<Vec2f, Vec2f[]>> anchors;
    private final List<Vec2f> points;
    private float left, right, step;
    public CatmullromCurveTemplate(float left, float right, float step,
                                   List<Vec2f> anchors) {
        this.anchors = new ArrayList<>(anchors.isEmpty() ? 0 : anchors.size() - 1);
        this.points = Lists.newArrayList();
        this.left = left;
        this.right = right;
        this.step = step;
        boolean flag = compileAnchors(anchors, false);
        if (flag) compileCurve(); else compileLine();
    }

    public boolean compileAnchors(List<Vec2f> anchors, boolean shouldClear) {
        if (shouldClear) this.anchors.clear();
        if (anchors.size() < 3) return false;
        for (int i = 0; i < anchors.size() - 1; i++) {
            Vec2f p1 = anchors.get(i);
            Vec2f p2 = anchors.get(i + 1);
            Vec2f[] controllers;
            if (i == 0) {
                Vec2f p3 = anchors.get(i + 2);
                controllers = CatmullRomUtils.last3PointsToCRSPoints(p1, p2, p3);
            } else if (i == anchors.size() - 2) {
                Vec2f p0 = anchors.get(i - 1);
                controllers = CatmullRomUtils.first3PointsToCRSPoints(p0, p1, p2);
            } else {
                Vec2f p0 = anchors.get(i - 1);
                Vec2f p3 = anchors.get(i + 2);
                controllers = CatmullRomUtils.genDefaultCRSPoints(p0, p1, p2, p3);
            }
            this.anchors.add(Pair.of(p1, controllers));
        }
        return true;
    }

    @Override
    public List<Vec2f> getPointList() {
        return points;
    }

    public void compileCurve() {
        points.clear();
        float length = right - left;
        if (length * step < 0) return;
        Vec2f p0, p1;
        for (int i = 0; i < anchors.size() - 1; i++) {
            p0 = anchors.get(i).getFirst();
            p1 = anchors.get(i + 1).getFirst();
            float l = Math.min(left, right);
            float r = Math.max(left, right);
            if (p0.x() < l && p1.x() < l) continue;
            if (p0.x() > r && p1.x() > r) continue;
            float pl = Math.min(p0.x(), p1.x());
            float pr = Math.max(p0.x(), p1.x());
        }
    }

    public void compileLine() {

    }

    @Override
    public Pair<Float, Float> getRange() {
        return Pair.of(left, right);
    }

    @Override
    public void setLeft(float left) {
        this.left = left;
    }

    @Override
    public void setRight(float right) {
        this.right = right;
    }

    @Override
    public float getStep() {
        return step;
    }
}
