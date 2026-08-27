import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import '../models/linkedin_profile.dart';
import '../widgets/articles_section.dart';
import '../services/api_service.dart';
import '../widgets/current_position_card.dart';
import '../widgets/education_section.dart';
import '../widgets/experience_section.dart';
import '../widgets/languages_section.dart';
import '../widgets/posts_section.dart';
import '../widgets/profile_header.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final TextEditingController _urlController = TextEditingController();
  final ApiService _apiService = const ApiService();
  HybridLinkedInProfileResponse? _profile;
  String? _errorMessage;
  bool _isLoading = false;

  Future<void> _openApiDocs() async {
    final docsUri = _apiService.swaggerUiUri;
    final opened =
        await launchUrl(docsUri, mode: LaunchMode.externalApplication);

    if (!opened) {
      setState(() {
        _errorMessage = 'Could not open API docs: $docsUri';
      });
    }
  }

  Future<void> _fetchProfile() async {
    FocusScope.of(context).unfocus();
    final profileUrl = _urlController.text.trim();
    if (profileUrl.isEmpty) {
      setState(() {
        _errorMessage = 'Please enter a LinkedIn profile URL.';
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
      _profile = null;
    });

    try {
      final profile = await _apiService.scrapeProfile(profileUrl);
      setState(() {
        _profile = profile;
      });
    } catch (error) {
      setState(() {
        _errorMessage = error.toString();
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  void dispose() {
    _urlController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leadingWidth: 72,
        leading: Padding(
          padding: const EdgeInsets.all(6),
          child: Image.asset('assets/linkedin-logo.png'),
        ),
        title: const Text('LinkedIn Scraper'),
        actions: [
          TextButton.icon(
            onPressed: _openApiDocs,
            icon: const Icon(Icons.description_outlined),
            label: const Text('API Docs'),
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              Theme.of(
                context,
              ).colorScheme.primaryContainer.withValues(alpha: 0.22),
              Theme.of(context).scaffoldBackgroundColor,
            ],
          ),
        ),
        child: Center(
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 940),
            child: ListView(
              padding: const EdgeInsets.all(16),
              children: [
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(14),
                    child: Row(
                      children: [
                        Expanded(
                          child: TextField(
                            controller: _urlController,
                            decoration: const InputDecoration(
                              prefixIcon: Icon(Icons.link),
                              labelText: 'LinkedIn Profile URL',
                              hintText: 'https://www.linkedin.com/in/username/',
                            ),
                            onSubmitted: (_) => _fetchProfile(),
                          ),
                        ),
                        const SizedBox(width: 12),
                        FilledButton.icon(
                          onPressed: _isLoading ? null : _fetchProfile,
                          icon: const Icon(Icons.search),
                          label: const Text('Fetch'),
                        ),
                      ],
                    ),
                  ),
                ),
                if (_isLoading)
                  const Padding(
                    padding: EdgeInsets.only(top: 24),
                    child: Center(child: CircularProgressIndicator()),
                  ),
                if (_errorMessage != null)
                  Padding(
                    padding: const EdgeInsets.only(top: 16),
                    child: Card(
                      color: Theme.of(context).colorScheme.errorContainer,
                      child: Padding(
                        padding: const EdgeInsets.all(12),
                        child: Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Icon(
                              Icons.error_outline,
                              color: Theme.of(context).colorScheme.error,
                            ),
                            const SizedBox(width: 10),
                            Expanded(
                              child: Text(
                                _errorMessage!,
                                style: TextStyle(
                                  color: Theme.of(context).colorScheme.error,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                if (_profile != null) ...[
                  const SizedBox(height: 16),
                  ProfileHeader(profile: _profile!),
                  const SizedBox(height: 12),
                  CurrentPositionCard(currentPosition: _profile!.currentPosition),
                  const SizedBox(height: 12),
                  LanguagesSection(languages: _profile!.languages),
                  const SizedBox(height: 12),
                  EducationSection(education: _profile!.education),
                  const SizedBox(height: 12),
                  ExperienceSection(experiences: _profile!.experiences),
                  const SizedBox(height: 12),
                  PostsSection(posts: _profile!.posts),
                  const SizedBox(height: 12),
                  ArticlesSection(articles: _profile!.articles),
                ],
              ],
            ),
          ),
        ),
      ),
    );
  }
}
