import java.util.Arrays;
import java.util.regex.Pattern;

public class PatternDemo1 {

	/**
	 * java.util.regex包负责对字符序列进行正则表达式匹配
	 * Pattern负责编译
	 * Matcher负责匹配
	 * ---------------
	 * 正则表达式：
	 *1、元字符 好比char，紧紧代表一个字符；
	 *2、某些字符，比如\、.、*等这样的字符在正则表达式中已经被作为标记使用了，如果你想作为非元字符使用的话就得进行转义，转义的方法是在这些字符前面加\，
	 * 比如\就变成\\，.就成为\.
	 *3、重复的元字符有：*,+,?,{n},{n,},{n,m}，这个标记是用来修饰它前面的那个字符的。
	 *4、字符类，或称为分组[],比如[0-9],[0-9A-Za-z]用-表示范围
	 */
		public static void main(String[] args) {
			/**1、元字符的[]*/
			//[]属于元字符系列，所谓元字符简单的说就是字符，就是一个char，像bc就不是一个char了
//			method2(new String[]{"abt","act","adt","abct","bt"}, "a[bcd]t");
			//[]的区间，-的两头代表区间，多区间这样写，比如[0-9a-zA-Z]
//			method2(new String[]{"a1t","a3t","a01t","abt","2t"}, "a[0-9]t");
			//补集用^表示，在二元运算符中这是非的意思,尽管用了^，at还是不能通过，所以元字符[]必须有且仅有一个字符
//			method2(new String[]{"a0t","a2t","abt","at"}, "a[^0246]t");
			
			/**2、元字符的? + * */
			//？的目标是它前面的一个字符，元字符？表示它前面的一个字符出现0次或1次
//			method2(new String[]{"","a","aa","at","a1t"}, "a?");
			//元字符+的目标是它前面的一个字符，它表示它前面的那个字符出现1次或多次
//			method2(new String[]{"","a","aa","aaaaa","at","a1t"}, "a+");
			//元字符*的目标是它前面的一个字符，它表示它前面那个字符出现0次或多次，*可以说是?和+的并集
//			method2(new String[]{"","a","aa","aaaaa","at","a1t"}, "a*");
			
			/** .元字符能匹配任何字符(换行\r除外)，所以用.*可以匹配换行外的任何字符串  */
//			method2(new String[]{"","a","aa","aaaaa","at","a1t","\t","\r","n"}, ".");
//			method2(new String[]{"","a","aa","aaaaa","at","a1t","\t","\r","n"}, ".*");
			
			/** 数量{n},{n,},{n,m}*/
			//{n}表示它前面的字符重复n次，并且只重复n次，重复n-1次或n+1次都是不可以的
//			method2(new String[]{"","a","aa","aaaaa","at","a1t"}, "a{2}");
			//{n,}是对{n}的扩展，表示重复n次(包含n次)以上
//			method2(new String[]{"","a","aa","aaaaa","at","a1t"}, "a{2,}");
			//{n,m}表示数目在n到m范围内,包含两头
//			method2(new String[]{"","a","aa","aaa","aaaa","aaaaa","at"}, "a{2,4}");
//			method2(new String[]{"12345-1234","12345"}, "\\d{5}-\\d{4}|\\d{5}");
			//
//			method2(new String[]{"12345-123","12345"}, "\\d{5}|\\d{5}-\\d{4}");
//			method2(new String[]{"211","12345"}, "2[0-4]\\d");
//			method2(new String[]{"1","12345"}, "[01]?\\d\\d?");
			method2(new String[]{"1","12345","12"}, "[0-9]");
			
		}
		private static void method2(String[] a, String regex){
			Pattern p = Pattern.compile(regex);
			for (int i = 0; i < a.length; i++) {
				System.out.println(a[i]+","+p.matcher(a[i]).matches());
			}
			method1();
		}
		private static void method1(){
			String str = "2011-11-12";
			String pat = "\\d{4}-\\d{2}-\\d{2}";
			System.out.println(Pattern.compile(pat).matcher(str).matches());
			
			String str1 = "a1b22c333d4444e55555f";
			//按数字来分割
			String[] str1Arr = Pattern.compile("\\d+").split(str1);
			System.out.println(Arrays.toString(str1Arr));
			//数字全部替换成_
			System.out.println(Pattern.compile("\\d+").matcher(str1).replaceAll("_"));
			//去除所有的空格
			System.out.println("\\s去除所有的空格:"+Pattern.compile("\\s").matcher("aa b c d ").replaceAll(""));
			System.out.println("5-10个字符:"+Pattern.compile(".{5,10}").matcher("12345").matches());
			System.out.println("多匹配:"+Pattern.compile("\\bhi.*Lucy\\b").matcher("hi后面不远处跟着一个Lucy").matches());
			System.out.println("多匹配2:"+Pattern.compile("^\\d{5,12}$").matcher("12345678a").matches());
			
			//直接调用String类提供的方法更加的方便
			System.out.println(Arrays.toString(str1.split("\\d+")));
			System.out.println("2011-11-12".matches("\\d{4}-\\d{2}-\\d{2}"));
		}
	}
