package com.romix.feature.feat427

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat427Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat427UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat427FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat427UserSummary
)

data class Feat427UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat427NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat427Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat427Config = Feat427Config()
) {

    fun loadSnapshot(userId: Long): Feat427NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat427NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat427UserSummary {
        return Feat427UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat427FeedItem> {
        val result = java.util.ArrayList<Feat427FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat427FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat427UiMapper {

    fun mapToUi(model: List<Feat427FeedItem>): Feat427UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat427UiModel(
            header = UiText("Feat427 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat427UiModel =
        Feat427UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat427UiModel =
        Feat427UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat427UiModel =
        Feat427UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat427Service(
    private val repository: Feat427Repository,
    private val uiMapper: Feat427UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat427UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat427UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat427UserItem1(val user: CoreUser, val label: String)
data class Feat427UserItem2(val user: CoreUser, val label: String)
data class Feat427UserItem3(val user: CoreUser, val label: String)
data class Feat427UserItem4(val user: CoreUser, val label: String)
data class Feat427UserItem5(val user: CoreUser, val label: String)
data class Feat427UserItem6(val user: CoreUser, val label: String)
data class Feat427UserItem7(val user: CoreUser, val label: String)
data class Feat427UserItem8(val user: CoreUser, val label: String)
data class Feat427UserItem9(val user: CoreUser, val label: String)
data class Feat427UserItem10(val user: CoreUser, val label: String)

data class Feat427StateBlock1(val state: Feat427UiModel, val checksum: Int)
data class Feat427StateBlock2(val state: Feat427UiModel, val checksum: Int)
data class Feat427StateBlock3(val state: Feat427UiModel, val checksum: Int)
data class Feat427StateBlock4(val state: Feat427UiModel, val checksum: Int)
data class Feat427StateBlock5(val state: Feat427UiModel, val checksum: Int)
data class Feat427StateBlock6(val state: Feat427UiModel, val checksum: Int)
data class Feat427StateBlock7(val state: Feat427UiModel, val checksum: Int)
data class Feat427StateBlock8(val state: Feat427UiModel, val checksum: Int)
data class Feat427StateBlock9(val state: Feat427UiModel, val checksum: Int)
data class Feat427StateBlock10(val state: Feat427UiModel, val checksum: Int)

fun buildFeat427UserItem(user: CoreUser, index: Int): Feat427UserItem1 {
    return Feat427UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat427StateBlock(model: Feat427UiModel): Feat427StateBlock1 {
    return Feat427StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat427UserSummary> {
    val list = java.util.ArrayList<Feat427UserSummary>(users.size)
    for (user in users) {
        list += Feat427UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat427UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat427UiModel {
    val summaries = (0 until count).map {
        Feat427UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat427UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat427UiModel> {
    val models = java.util.ArrayList<Feat427UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat427AnalyticsEvent1(val name: String, val value: String)
data class Feat427AnalyticsEvent2(val name: String, val value: String)
data class Feat427AnalyticsEvent3(val name: String, val value: String)
data class Feat427AnalyticsEvent4(val name: String, val value: String)
data class Feat427AnalyticsEvent5(val name: String, val value: String)
data class Feat427AnalyticsEvent6(val name: String, val value: String)
data class Feat427AnalyticsEvent7(val name: String, val value: String)
data class Feat427AnalyticsEvent8(val name: String, val value: String)
data class Feat427AnalyticsEvent9(val name: String, val value: String)
data class Feat427AnalyticsEvent10(val name: String, val value: String)

fun logFeat427Event1(event: Feat427AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat427Event2(event: Feat427AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat427Event3(event: Feat427AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat427Event4(event: Feat427AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat427Event5(event: Feat427AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat427Event6(event: Feat427AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat427Event7(event: Feat427AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat427Event8(event: Feat427AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat427Event9(event: Feat427AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat427Event10(event: Feat427AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat427Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat427Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat427Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat427Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat427Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat427Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat427Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat427Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat427Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat427Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat427(u: CoreUser): Feat427Projection1 =
    Feat427Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat427Projection1> {
    val list = java.util.ArrayList<Feat427Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat427(u)
    }
    return list
}
