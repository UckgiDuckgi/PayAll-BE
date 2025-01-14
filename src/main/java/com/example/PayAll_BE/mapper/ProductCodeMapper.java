package com.example.PayAll_BE.mapper;

import java.util.Map;

public class ProductCodeMapper {
	public static final Map<String, String> PRODUCT_MAP = Map.ofEntries(
		Map.entry("4060647", "삼다수 2L (24개)"),
		Map.entry("1026291", "신라면 (5개)"),
		Map.entry("16494830", "코카콜라음료 코카콜라 제로 1.25L (12개)"),
		Map.entry("2085488", "CJ제일제당 햇반 흑미밥 210g (36개)"),
		Map.entry("4734659", "CJ제일제당 비비고 왕교자 1.05kg (1개)"),
		Map.entry("1128841", "깨끗한나라 순수 프리미엄 30m (30롤) *3팩"),
		Map.entry("27756731", "센카 퍼펙트 휩 페이셜 워시"),
		Map.entry("19879892", "퍼실 딥클린 라벤더젤 겸용 2.7L (1개)"),
		Map.entry("7626574", "코멧 오리지널 물티슈 캡형 100매 (10팩)"),
		Map.entry("5361457", "페리오 뉴 후레쉬 치약 150g (벌크) (20개)"),
		Map.entry("1754500", "BYC 기획 신사 양말 (10켤레)"),
		Map.entry("3093363", "동원F&B 고추참치 100g (1개)"),
		Map.entry("1109302", "물먹는하마 옷장용 300g (16개)"),
		Map.entry("7090609", "산과들에 한줌견과 원데이 프리미엄 20g (100개)"),
		Map.entry("9637956", "닥터포헤어 폴리젠 플러스 샴푸 500ml (1개)"),
		Map.entry("12673118", "다봉산업 마이핫 보온대 대용량 160g (15개)"),
		Map.entry("2012426", "고려은단 비타민C 1000 600정 (1개)"),
		Map.entry("16454519", "오리온 닥터유 단백질바 미니 44개입 594g (1개)"),
		Map.entry("1992016", "삼양식품 불닭볶음면 140g (5개)"),
		Map.entry("1008149", "서울우유 흰우유 1L (멸균) (10개)")
	);

	public String getProductNameByCode(String productCode) {
		return PRODUCT_MAP.getOrDefault(productCode, "상품 정보를 찾을 수 없습니다.");
	}
}
