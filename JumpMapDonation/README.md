# JumpMapDonation

SSAPI-Minecraft와 연동해서, 후원 금액에 따라 점프맵 방해 효과를 발동시키는 플러그인입니다.

## 항목 (가격 설정용 key)
즉사 `instant-death` / 태초 `reset` / 디버프 제거 `debuff-remove` /
체력 1칸 제거 `health-down` / 구속 `bind` / 독 `poison` / 실명 `blindness` /
배고픔 1칸 제거 `hunger-down` / 조작키 반전 `reverse-controls` / 밀치기 `knockback`

## 빌드 (jar 만들기)
JDK 17 + Gradle 이 있으면: `gradle build` → `build/libs/JumpMapDonation-1.0.0.jar`
GitHub에 올리면 `.github/workflows/build.yml` 이 자동으로 빌드해서 Actions 탭에서 jar를 받을 수 있습니다.
jar를 서버 `plugins/` 폴더에 넣으세요. Paper API 버전은 build.gradle 에서 서버 버전에 맞게 조정하세요.

## SSAPI-Minecraft 연동
SSAPI config.yml:
```yaml
commands:
  donation:
    enabled: true
    format: "jumpmapdonate {player} {amount} {donator_name}"
```

## 명령어
```
/jmprice list
/jmprice set <항목키> <가격>      예: /jmprice set poison 400
/jmprice mode <EXACT|THRESHOLD>
/jmprice spawn                    '태초' 위치를 현재 위치로 저장 (권한: jumpmapdonation.admin)
```

## 참고
- '조작키 반전'과 '밀치기'는 서버에서 이동을 보정하는 방식이라 클라이언트 키 입력 자체를 바꾸지는 않습니다.
- 설치 후 반드시 점프맵 시작 지점에서 `/jmprice spawn` 을 실행하세요.
