stanfordParserTest
================== 
step by step

一. 文本预处理
	Input:
		文本摘要
	Output:
		得到文本摘要中含有两个或两个以上蛋白质的句子。
	Procedure:
		1. 对给的文本摘要进行分句。依据(.!?)进行划分句子。
		2. 识别出每一个句子中的蛋白质。(这里需要改进, 需要借鉴CRF等Machine Learning等方法进行实体识别)
			保存该句子、蛋白质名称、官方基因名称、以及在该句子中的位置、该句子中识别出蛋白质的个数
		3. 将识别出的蛋白质转化为官方名称的基因名称。
			将2中识别出来的蛋白质利用蛋白质-Gene转换成官方基因名称Gene
		4. 输出包含至少两个Gene的句子。
	For example:
	
	->pombe Translin, which was cloned, revealed that it selectively binds d(GT)n and d(GTT)n microsatellite repeats.
	
	->[pombe, Translin, ,, which, was, cloned, revealed, that, it, selectively, binds, d, (, GT, ), n, and, d, (, GTT, ), n, microsatellite, repeats, .]
	
	->[pombe, TSN, ,, which, was, cloned, revealed, that, it, selectively, binds, d, (, ITGA2B, ), n, and, d, (, GTT, ), n, microsatellite, repeats, .]
	
	->pombe TSN, which was cloned, revealed that it selectively binds d(ITGA2B)n and d(GTT)n microsatellite repeats.


二. 得到候选蛋白质相互作用对
	使用Standford Parser对一中所得句子进行解析 -->> 得到语法树
	使用Tregex定义规则结合关系词字典识别出如下三种情况下的相互作用关系.
	
		Form 1: PIP, protein1 relation protein2.
		Form 2: IPP, relation protein1 protein2.
		Form 3: PPI, protein1 protein2 relation.
	
	1. 识别出关系词;
	2. 识别出否定词;
	3. 根据下面6项包括7条规则得到候选蛋白质的相互作用对;
		1> 	IPP, PPI: 关系词I常为NN*(名词): 例如, A and B Interaction
			PIP: 关系词I常为VP*(动词): 例如, A interacted with B
		
		2> 三种形式中蛋白质实体之间词的个数.
			PPI, PP之间常为1个词; IPP, PP之间常为1个或没有词. (这点不是很正确)
		
		3> two proteins, a relation keyword; (similar with 2)
			two proteins, a relation keyword, a negation keyword.
			For example:
				a. A is not interacted with B.
				b. A and B is not interaction.
				c. not interaction A and B.
		
		4> more than two proteins and a relation keyword.
		use an algorithm: (Rule 5)
			1) 标出句子中每个单词的位置, 从0开始.
			2) 使用Hashtable保存蛋白质、关系词以及他们的位置.
			3) 识别出hashtable中的关系词.
			4) 通过结合关系词和出现在它前面和后面位置的蛋白质, 得到三元组.
			5) 得到所有的PPIs.
		
		5> more than two proteins, a relation keyword and a negation keyword.
			类似于4, 只是需要额外检测 negation keyword.
			例如: 蛋白质后跟着否定词可以被认为是false PPIs.
		
		6> more than two proteins and two negation keywords.
			一般这种情况是包含Neither/Nor的句子.
	
三. 
