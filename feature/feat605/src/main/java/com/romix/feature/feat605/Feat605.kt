package com.romix.feature.feat605

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat605Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat605UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat605FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat605UserSummary
)

data class Feat605UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat605NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat605Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat605Config = Feat605Config()
) {

    fun loadSnapshot(userId: Long): Feat605NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat605NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat605UserSummary {
        return Feat605UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat605FeedItem> {
        val result = java.util.ArrayList<Feat605FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat605FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat605UiMapper {

    fun mapToUi(model: List<Feat605FeedItem>): Feat605UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat605UiModel(
            header = UiText("Feat605 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat605UiModel =
        Feat605UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat605UiModel =
        Feat605UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat605UiModel =
        Feat605UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat605Service(
    private val repository: Feat605Repository,
    private val uiMapper: Feat605UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat605UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat605UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat605UserItem1(val user: CoreUser, val label: String)
data class Feat605UserItem2(val user: CoreUser, val label: String)
data class Feat605UserItem3(val user: CoreUser, val label: String)
data class Feat605UserItem4(val user: CoreUser, val label: String)
data class Feat605UserItem5(val user: CoreUser, val label: String)
data class Feat605UserItem6(val user: CoreUser, val label: String)
data class Feat605UserItem7(val user: CoreUser, val label: String)
data class Feat605UserItem8(val user: CoreUser, val label: String)
data class Feat605UserItem9(val user: CoreUser, val label: String)
data class Feat605UserItem10(val user: CoreUser, val label: String)

data class Feat605StateBlock1(val state: Feat605UiModel, val checksum: Int)
data class Feat605StateBlock2(val state: Feat605UiModel, val checksum: Int)
data class Feat605StateBlock3(val state: Feat605UiModel, val checksum: Int)
data class Feat605StateBlock4(val state: Feat605UiModel, val checksum: Int)
data class Feat605StateBlock5(val state: Feat605UiModel, val checksum: Int)
data class Feat605StateBlock6(val state: Feat605UiModel, val checksum: Int)
data class Feat605StateBlock7(val state: Feat605UiModel, val checksum: Int)
data class Feat605StateBlock8(val state: Feat605UiModel, val checksum: Int)
data class Feat605StateBlock9(val state: Feat605UiModel, val checksum: Int)
data class Feat605StateBlock10(val state: Feat605UiModel, val checksum: Int)

fun buildFeat605UserItem(user: CoreUser, index: Int): Feat605UserItem1 {
    return Feat605UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat605StateBlock(model: Feat605UiModel): Feat605StateBlock1 {
    return Feat605StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat605UserSummary> {
    val list = java.util.ArrayList<Feat605UserSummary>(users.size)
    for (user in users) {
        list += Feat605UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat605UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat605UiModel {
    val summaries = (0 until count).map {
        Feat605UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat605UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat605UiModel> {
    val models = java.util.ArrayList<Feat605UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat605AnalyticsEvent1(val name: String, val value: String)
data class Feat605AnalyticsEvent2(val name: String, val value: String)
data class Feat605AnalyticsEvent3(val name: String, val value: String)
data class Feat605AnalyticsEvent4(val name: String, val value: String)
data class Feat605AnalyticsEvent5(val name: String, val value: String)
data class Feat605AnalyticsEvent6(val name: String, val value: String)
data class Feat605AnalyticsEvent7(val name: String, val value: String)
data class Feat605AnalyticsEvent8(val name: String, val value: String)
data class Feat605AnalyticsEvent9(val name: String, val value: String)
data class Feat605AnalyticsEvent10(val name: String, val value: String)

fun logFeat605Event1(event: Feat605AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat605Event2(event: Feat605AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat605Event3(event: Feat605AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat605Event4(event: Feat605AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat605Event5(event: Feat605AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat605Event6(event: Feat605AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat605Event7(event: Feat605AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat605Event8(event: Feat605AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat605Event9(event: Feat605AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat605Event10(event: Feat605AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat605Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat605Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat605Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat605Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat605Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat605Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat605Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat605Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat605Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat605Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat605(u: CoreUser): Feat605Projection1 =
    Feat605Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat605Projection1> {
    val list = java.util.ArrayList<Feat605Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat605(u)
    }
    return list
}
