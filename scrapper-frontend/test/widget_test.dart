import 'package:flutter_test/flutter_test.dart';
import 'package:scrapper_frontend/main.dart';

void main() {
  testWidgets('app renders', (WidgetTester tester) async {
    await tester.pumpWidget(const LinkedInScrapperApp());
    expect(find.text('Scrapper'), findsOneWidget);
  });
}
