import 'package:flutter_test/flutter_test.dart';
import 'package:scraper_frontend/main.dart';

void main() {
  testWidgets('app renders', (WidgetTester tester) async {
    await tester.pumpWidget(const LinkedInScraperApp());
    expect(find.text('LinkedIn Scraper'), findsOneWidget);
    expect(find.text('Fetch'), findsOneWidget);
  });
}
