package com.romix.feature.feat467

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat467Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat467UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat467FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat467UserSummary
)

data class Feat467UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat467NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat467Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat467Config = Feat467Config()
) {

    fun loadSnapshot(userId: Long): Feat467NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat467NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat467UserSummary {
        return Feat467UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat467FeedItem> {
        val result = java.util.ArrayList<Feat467FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat467FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat467UiMapper {

    fun mapToUi(model: List<Feat467FeedItem>): Feat467UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat467UiModel(
            header = UiText("Feat467 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat467UiModel =
        Feat467UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat467UiModel =
        Feat467UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat467UiModel =
        Feat467UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat467Service(
    private val repository: Feat467Repository,
    private val uiMapper: Feat467UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat467UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat467UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat467UserItem1(val user: CoreUser, val label: String)
data class Feat467UserItem2(val user: CoreUser, val label: String)
data class Feat467UserItem3(val user: CoreUser, val label: String)
data class Feat467UserItem4(val user: CoreUser, val label: String)
data class Feat467UserItem5(val user: CoreUser, val label: String)
data class Feat467UserItem6(val user: CoreUser, val label: String)
data class Feat467UserItem7(val user: CoreUser, val label: String)
data class Feat467UserItem8(val user: CoreUser, val label: String)
data class Feat467UserItem9(val user: CoreUser, val label: String)
data class Feat467UserItem10(val user: CoreUser, val label: String)

data class Feat467StateBlock1(val state: Feat467UiModel, val checksum: Int)
data class Feat467StateBlock2(val state: Feat467UiModel, val checksum: Int)
data class Feat467StateBlock3(val state: Feat467UiModel, val checksum: Int)
data class Feat467StateBlock4(val state: Feat467UiModel, val checksum: Int)
data class Feat467StateBlock5(val state: Feat467UiModel, val checksum: Int)
data class Feat467StateBlock6(val state: Feat467UiModel, val checksum: Int)
data class Feat467StateBlock7(val state: Feat467UiModel, val checksum: Int)
data class Feat467StateBlock8(val state: Feat467UiModel, val checksum: Int)
data class Feat467StateBlock9(val state: Feat467UiModel, val checksum: Int)
data class Feat467StateBlock10(val state: Feat467UiModel, val checksum: Int)

fun buildFeat467UserItem(user: CoreUser, index: Int): Feat467UserItem1 {
    return Feat467UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat467StateBlock(model: Feat467UiModel): Feat467StateBlock1 {
    return Feat467StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat467UserSummary> {
    val list = java.util.ArrayList<Feat467UserSummary>(users.size)
    for (user in users) {
        list += Feat467UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat467UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat467UiModel {
    val summaries = (0 until count).map {
        Feat467UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat467UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat467UiModel> {
    val models = java.util.ArrayList<Feat467UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat467AnalyticsEvent1(val name: String, val value: String)
data class Feat467AnalyticsEvent2(val name: String, val value: String)
data class Feat467AnalyticsEvent3(val name: String, val value: String)
data class Feat467AnalyticsEvent4(val name: String, val value: String)
data class Feat467AnalyticsEvent5(val name: String, val value: String)
data class Feat467AnalyticsEvent6(val name: String, val value: String)
data class Feat467AnalyticsEvent7(val name: String, val value: String)
data class Feat467AnalyticsEvent8(val name: String, val value: String)
data class Feat467AnalyticsEvent9(val name: String, val value: String)
data class Feat467AnalyticsEvent10(val name: String, val value: String)

fun logFeat467Event1(event: Feat467AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat467Event2(event: Feat467AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat467Event3(event: Feat467AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat467Event4(event: Feat467AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat467Event5(event: Feat467AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat467Event6(event: Feat467AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat467Event7(event: Feat467AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat467Event8(event: Feat467AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat467Event9(event: Feat467AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat467Event10(event: Feat467AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat467Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat467Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat467Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat467Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat467Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat467Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat467Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat467Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat467Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat467Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat467(u: CoreUser): Feat467Projection1 =
    Feat467Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat467Projection1> {
    val list = java.util.ArrayList<Feat467Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat467(u)
    }
    return list
}
