package com.patrones.u2;

import java.util.List;
import java.util.Locale;

public class HtmlReportBody implements ReportBody {
    public String render(List<GradeRecord> records) {
        StringBuilder sb = new StringBuilder("[HTML:cuerpo] <table>\n");
        for (GradeRecord r : records) {
            sb.append(String.format(Locale.US, "  <tr><td>%s</td><td>%s</td><td>%.1f</td></tr>%n",
                    r.getStudentName(), r.getCourseCode(), r.getGrade()));
        }
        sb.append("</table>");
        return sb.toString();
    }
}
