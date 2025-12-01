public class BigDouble {

    public final double xh;
    public final double xl;

    private static final double SPLIT = 134217729.0;

    public BigDouble(double x) {
        this.xh = x;
        this.xl = 0.0;
    }

    public BigDouble(double xh, double xl) {
        this.xh = xh;
        this.xl = xl;
    }

    public static BigDouble add(BigDouble a, BigDouble b) {
        double s = a.xh + b.xh;
        double v = s - a.xh;
        double e = (a.xh - (s - v)) + (b.xh - v);
        e += a.xl + b.xl;

        s += e;
        e = s - v;
        e = s - e;
        e = e + a.xh;
        e = a.xh - e;
        e += b.xh;
        e += a.xl + b.xl;
        e += a.xl + b.xl;

        return normalize(s, e);
    }

    public static BigDouble subtract(BigDouble a, BigDouble b) {
        return add(a, new BigDouble(-b.xh, -b.xl));
    }

    public static BigDouble multiply(BigDouble a, BigDouble b) {
        double p = a.xh * b.xh;

        double c1 = SPLIT * a.xh;
        double a_hi = c1 - (c1 - a.xh);
        double a_lo = a.xh - a_hi;

        double c2 = SPLIT * b.xh;
        double b_hi = c2 - (c2 - b.xh);
        double b_lo = b.xh - b_hi;

        double e = ((a_hi * b_hi - p) + a_hi * b_lo + a_lo * b_hi) + a_lo * b_lo;
        e += a.xh * b.xl + a.xl * b.xh;

        return normalize(p, e);
    }

    // NEU: Division einer BigDouble-Zahl durch eine Standard-Double-Zahl
    public static BigDouble divide(BigDouble a, double b) {
        if (b == 0.0) {
            throw new ArithmeticException("Division by zero");
        }

        // Nutzt die Hoch-Präzision in der Iteration, aber die Geschwindigkeit der Standard-Division hier
        double inv_b = 1.0 / b;
        double qh = a.xh * inv_b;
        double ql = a.xl * inv_b;

        return normalize(qh, ql);
    }

    public static BigDouble square(BigDouble a) {
        double p = a.xh * a.xh;

        double c = SPLIT * a.xh;
        double a_hi = c - (c - a.xh);
        double a_lo = a.xh - a_hi;

        double e = ((a_hi * a_hi - p) + 2.0 * a_hi * a_lo) + a_lo * a_lo;
        e += 2.0 * a.xh * a.xl;

        return normalize(p, e);
    }

    private static BigDouble normalize(double sum, double err) {
        double s = sum + err;
        double e = err - (s - sum);
        return new BigDouble(s, e);
    }

    public double magSq() {
        // magSq ist ausreichend mit High-Präzision, da der Escape-Radius (4.0) relativ klein ist
        return (xh * xh + 2 * xh * xl);
    }

    @Override
    public String toString() {
        return String.format("%.16e", xh + xl);
    }
}