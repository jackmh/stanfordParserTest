import java.util.Arrays;
import java.util.regex.Pattern;

public class PatternDemo1 {

	/**
	 * java.util.regex��������ַ����н���������ʽƥ��
	 * Pattern�������
	 * Matcher����ƥ��
	 * ---------------
	 * ������ʽ��
	 *1��Ԫ�ַ� �ñ�char����������һ���ַ���
	 *2��ĳЩ�ַ�������\��.��*���������ַ���������ʽ���Ѿ�����Ϊ���ʹ���ˣ����������Ϊ��Ԫ�ַ�ʹ�õĻ��͵ý���ת�壬ת��ķ���������Щ�ַ�ǰ���\��
	 * ����\�ͱ��\\��.�ͳ�Ϊ\.
	 *3���ظ���Ԫ�ַ��У�*,+,?,{n},{n,},{n,m}��������������������ǰ����Ǹ��ַ��ġ�
	 *4���ַ��࣬���Ϊ����[],����[0-9],[0-9A-Za-z]��-��ʾ��Χ
	 */
		public static void main(String[] args) {
			/**1��Ԫ�ַ���[]*/
			//[]����Ԫ�ַ�ϵ�У���νԪ�ַ��򵥵�˵�����ַ�������һ��char����bc�Ͳ���һ��char��
//			method2(new String[]{"abt","act","adt","abct","bt"}, "a[bcd]t");
			//[]�����䣬-����ͷ�������䣬����������д������[0-9a-zA-Z]
//			method2(new String[]{"a1t","a3t","a01t","abt","2t"}, "a[0-9]t");
			//������^��ʾ���ڶ�Ԫ����������Ƿǵ���˼,��������^��at���ǲ���ͨ��������Ԫ�ַ�[]�������ҽ���һ���ַ�
//			method2(new String[]{"a0t","a2t","abt","at"}, "a[^0246]t");
			
			/**2��Ԫ�ַ���? + * */
			//����Ŀ������ǰ���һ���ַ���Ԫ�ַ�����ʾ��ǰ���һ���ַ�����0�λ�1��
//			method2(new String[]{"","a","aa","at","a1t"}, "a?");
			//Ԫ�ַ�+��Ŀ������ǰ���һ���ַ�������ʾ��ǰ����Ǹ��ַ�����1�λ���
//			method2(new String[]{"","a","aa","aaaaa","at","a1t"}, "a+");
			//Ԫ�ַ�*��Ŀ������ǰ���һ���ַ�������ʾ��ǰ���Ǹ��ַ�����0�λ��Σ�*����˵��?��+�Ĳ���
//			method2(new String[]{"","a","aa","aaaaa","at","a1t"}, "a*");
			
			/** .Ԫ�ַ���ƥ���κ��ַ�(����\r����)��������.*����ƥ�任������κ��ַ���  */
//			method2(new String[]{"","a","aa","aaaaa","at","a1t","\t","\r","n"}, ".");
//			method2(new String[]{"","a","aa","aaaaa","at","a1t","\t","\r","n"}, ".*");
			
			/** ����{n},{n,},{n,m}*/
			//{n}��ʾ��ǰ����ַ��ظ�n�Σ�����ֻ�ظ�n�Σ��ظ�n-1�λ�n+1�ζ��ǲ����Ե�
//			method2(new String[]{"","a","aa","aaaaa","at","a1t"}, "a{2}");
			//{n,}�Ƕ�{n}����չ����ʾ�ظ�n��(����n��)����
//			method2(new String[]{"","a","aa","aaaaa","at","a1t"}, "a{2,}");
			//{n,m}��ʾ��Ŀ��n��m��Χ��,������ͷ
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
			//���������ָ�
			String[] str1Arr = Pattern.compile("\\d+").split(str1);
			System.out.println(Arrays.toString(str1Arr));
			//����ȫ���滻��_
			System.out.println(Pattern.compile("\\d+").matcher(str1).replaceAll("_"));
			//ȥ�����еĿո�
			System.out.println("\\sȥ�����еĿո�:"+Pattern.compile("\\s").matcher("aa b c d ").replaceAll(""));
			System.out.println("5-10���ַ�:"+Pattern.compile(".{5,10}").matcher("12345").matches());
			System.out.println("��ƥ��:"+Pattern.compile("\\bhi.*Lucy\\b").matcher("hi���治Զ������һ��Lucy").matches());
			System.out.println("��ƥ��2:"+Pattern.compile("^\\d{5,12}$").matcher("12345678a").matches());
			
			//ֱ�ӵ���String���ṩ�ķ������ӵķ���
			System.out.println(Arrays.toString(str1.split("\\d+")));
			System.out.println("2011-11-12".matches("\\d{4}-\\d{2}-\\d{2}"));
		}
	}
