import 'dart:convert';

import 'package:http/http.dart' as http;

import '../models/linkedin_profile.dart';

class ApiService {
  final String baseUrl;

  const ApiService({String? baseUrl})
      : baseUrl =
            baseUrl ??
            const String.fromEnvironment('API_BASE_URL', defaultValue: '');

  bool get _hasBaseUrl => baseUrl.trim().isNotEmpty;

  String get _normalizedBaseUrl =>
      baseUrl.endsWith('/') ? baseUrl.substring(0, baseUrl.length - 1) : baseUrl;

  Uri buildUri(String path, {Map<String, String>? queryParameters}) {
    if (!_hasBaseUrl) {
      return Uri(path: path, queryParameters: queryParameters);
    }

    return Uri.parse('$_normalizedBaseUrl$path')
        .replace(queryParameters: queryParameters);
  }

  Uri get swaggerUiUri => _hasBaseUrl
      ? Uri.parse('$_normalizedBaseUrl/swagger-ui/index.html')
      : Uri.base.resolve('/swagger-ui/index.html');

  Future<HybridLinkedInProfileResponse> scrapeProfile(String profileUrl) async {
    final uri = buildUri('/api/linkedin');
    final response = await http.post(
      uri,
      headers: const {'Content-Type': 'application/json'},
      body: jsonEncode({'url': profileUrl}),
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to fetch profile (${response.statusCode})');
    }

    final body = jsonDecode(response.body) as Map<String, dynamic>;
    return HybridLinkedInProfileResponse.fromJson(body);
  }
}
