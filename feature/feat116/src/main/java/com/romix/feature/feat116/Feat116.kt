package com.romix.feature.feat116

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat116Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat116UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat116FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat116UserSummary
)

data class Feat116UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat116NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat116Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat116Config = Feat116Config()
) {

    fun loadSnapshot(userId: Long): Feat116NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat116NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat116UserSummary {
        return Feat116UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat116FeedItem> {
        val result = java.util.ArrayList<Feat116FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat116FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat116UiMapper {

    fun mapToUi(model: List<Feat116FeedItem>): Feat116UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat116UiModel(
            header = UiText("Feat116 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat116UiModel =
        Feat116UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat116UiModel =
        Feat116UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat116UiModel =
        Feat116UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat116Service(
    private val repository: Feat116Repository,
    private val uiMapper: Feat116UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat116UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat116UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat116UserItem1(val user: CoreUser, val label: String)
data class Feat116UserItem2(val user: CoreUser, val label: String)
data class Feat116UserItem3(val user: CoreUser, val label: String)
data class Feat116UserItem4(val user: CoreUser, val label: String)
data class Feat116UserItem5(val user: CoreUser, val label: String)
data class Feat116UserItem6(val user: CoreUser, val label: String)
data class Feat116UserItem7(val user: CoreUser, val label: String)
data class Feat116UserItem8(val user: CoreUser, val label: String)
data class Feat116UserItem9(val user: CoreUser, val label: String)
data class Feat116UserItem10(val user: CoreUser, val label: String)

data class Feat116StateBlock1(val state: Feat116UiModel, val checksum: Int)
data class Feat116StateBlock2(val state: Feat116UiModel, val checksum: Int)
data class Feat116StateBlock3(val state: Feat116UiModel, val checksum: Int)
data class Feat116StateBlock4(val state: Feat116UiModel, val checksum: Int)
data class Feat116StateBlock5(val state: Feat116UiModel, val checksum: Int)
data class Feat116StateBlock6(val state: Feat116UiModel, val checksum: Int)
data class Feat116StateBlock7(val state: Feat116UiModel, val checksum: Int)
data class Feat116StateBlock8(val state: Feat116UiModel, val checksum: Int)
data class Feat116StateBlock9(val state: Feat116UiModel, val checksum: Int)
data class Feat116StateBlock10(val state: Feat116UiModel, val checksum: Int)

fun buildFeat116UserItem(user: CoreUser, index: Int): Feat116UserItem1 {
    return Feat116UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat116StateBlock(model: Feat116UiModel): Feat116StateBlock1 {
    return Feat116StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat116UserSummary> {
    val list = java.util.ArrayList<Feat116UserSummary>(users.size)
    for (user in users) {
        list += Feat116UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat116UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat116UiModel {
    val summaries = (0 until count).map {
        Feat116UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat116UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat116UiModel> {
    val models = java.util.ArrayList<Feat116UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat116AnalyticsEvent1(val name: String, val value: String)
data class Feat116AnalyticsEvent2(val name: String, val value: String)
data class Feat116AnalyticsEvent3(val name: String, val value: String)
data class Feat116AnalyticsEvent4(val name: String, val value: String)
data class Feat116AnalyticsEvent5(val name: String, val value: String)
data class Feat116AnalyticsEvent6(val name: String, val value: String)
data class Feat116AnalyticsEvent7(val name: String, val value: String)
data class Feat116AnalyticsEvent8(val name: String, val value: String)
data class Feat116AnalyticsEvent9(val name: String, val value: String)
data class Feat116AnalyticsEvent10(val name: String, val value: String)

fun logFeat116Event1(event: Feat116AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat116Event2(event: Feat116AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat116Event3(event: Feat116AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat116Event4(event: Feat116AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat116Event5(event: Feat116AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat116Event6(event: Feat116AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat116Event7(event: Feat116AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat116Event8(event: Feat116AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat116Event9(event: Feat116AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat116Event10(event: Feat116AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat116Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat116Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat116Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat116Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat116Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat116Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat116Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat116Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat116Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat116Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat116(u: CoreUser): Feat116Projection1 =
    Feat116Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat116Projection1> {
    val list = java.util.ArrayList<Feat116Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat116(u)
    }
    return list
}
