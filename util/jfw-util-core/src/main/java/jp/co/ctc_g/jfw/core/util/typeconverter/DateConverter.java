/*
 * Copyright (c) 2013 ITOCHU Techno-Solutions Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.ctc_g.jfw.core.util.typeconverter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.co.ctc_g.jfw.core.internal.Config;
import jp.co.ctc_g.jfw.core.internal.CoreInternals;
import jp.co.ctc_g.jfw.core.util.Dates;
import jp.co.ctc_g.jfw.core.util.Strings;

/**
 * <p>
 * {@link Date}に変換するためのコンバータクラスです。
 * </p>
 * <h4>クラスコンフィグオーバライド</h4>
 * <p>
 * 以下の{@link Config クラスコンフィグオーバライド}用のキーが公開されています。
 * </p>
 * <table class="property_file_override_info">
 *  <thead>
 *   <tr>
 *    <th>キー</th>
 *    <th>型</th>
 *    <th>効果</th>
 *    <th>デフォルト</th>
 *   </tr>
 *  </thead>
 *  <tbody>
 *   <tr>
 *    <td>patterns</td>
 *    <td>java.lang.String</td>
 *    <td>
 *      文字列から{@link Date}を生成する際の変換パターン定義です。
 *      複数ある場合には、カンマで区切ります。
 *    </td>
 *    <td>
 *     yyyy/MM/dd HH:mm:ss.SSS,yyyy/MM/dd,yyyy-MM-dd HH:mm:ss.SSS,yyyy-MM-dd,yyyy/MM,yyyy-MM
 *    </td>
 *   </tr>
 *  </tbody>
 * </table>
 * @author ITOCHU Techno-Solutions Corporation.
 */
public class DateConverter implements TypeConverter<Date> {

    /**
     * 文字列から{@link Date}を生成する際の変換パターンです。
     */
    private static final String[] PATTERNS;
    static {
        Config config = CoreInternals.getConfig(DateConverter.class);
        PATTERNS = config.find("patterns").split(",");
    };

    /**
     * デフォルトコンストラクタです。
     */
    public DateConverter() {}

    /**
     * {@link java.sql.Date}を変換します。
     * @param d 変換対象
     * @return 変換後
     */
    public Date convert(java.sql.Date d) {
        return new Date(d.getTime());
    }

    /**
     * {@link Timestamp}を変換します。
     * @param t 変換対象
     * @return 変換後
     */
    public Date convert(Timestamp t) {
        return new Date(t.getTime());
    }

    /**
     * {@link String}を変換します。
     * @param v 変換対象
     * @return 変換後
     * @throws TypeConversionException 方変換に失敗した場合（E-UTIL-TC#0001）
     */
    public Date convert(String v) {
        if (Strings.isEmpty(v)) return null;
        Date date = null;
        for (String p : PATTERNS) {
            date = Dates.makeFrom(v, p);
            if (date != null) break;
        }
        if (date == null) {
            Map<String, String> replace = new HashMap<String, String>(2);
            replace.put("target", v);
            replace.put("type", Date.class.getName());
            replace.put("patterns", Strings.joinBy(", ", PATTERNS));
            throw new TypeConversionException("E-UTIL-TC#0001", DateConverter.class, replace);
        }
        return date;
    }
}
