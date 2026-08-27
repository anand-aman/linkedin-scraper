import 'package:flutter/material.dart';

import '../models/linkedin_profile.dart';

class ArticlesSection extends StatelessWidget {
  final List<HybridArticle> articles;

  const ArticlesSection({super.key, required this.articles});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Articles', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            if (articles.isEmpty)
              const Text('null')
            else
              ...articles.asMap().entries.map(
                    (entry) => Padding(
                      padding: const EdgeInsets.only(bottom: 12),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Article ${entry.key + 1}',
                            style: Theme.of(context).textTheme.bodyLarge,
                          ),
                          const SizedBox(height: 4),
                          _field('Title', entry.value.title),
                          _field('URL', entry.value.url),
                          _field('Published Date', entry.value.publishedDate),
                          _field('Image', entry.value.image),
                          _field('Likes', entry.value.likes?.toString()),
                        ],
                      ),
                    ),
                  ),
          ],
        ),
      ),
    );
  }

  Widget _field(String label, String? value) {
    final displayValue = value ?? 'null';
    return Padding(
      padding: const EdgeInsets.only(bottom: 4),
      child: RichText(
        text: TextSpan(
          style: const TextStyle(color: Colors.black87),
          children: [
            TextSpan(
              text: '$label: ',
              style: const TextStyle(fontWeight: FontWeight.w600),
            ),
            TextSpan(text: displayValue),
          ],
        ),
      ),
    );
  }
}
