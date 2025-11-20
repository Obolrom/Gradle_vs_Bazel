package com.romix.feature.feat696

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat696Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat696UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat696FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat696UserSummary
)

data class Feat696UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat696NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat696Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat696Config = Feat696Config()
) {

    fun loadSnapshot(userId: Long): Feat696NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat696NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat696UserSummary {
        return Feat696UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat696FeedItem> {
        val result = java.util.ArrayList<Feat696FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat696FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat696UiMapper {

    fun mapToUi(model: List<Feat696FeedItem>): Feat696UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat696UiModel(
            header = UiText("Feat696 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat696UiModel =
        Feat696UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat696UiModel =
        Feat696UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat696UiModel =
        Feat696UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat696Service(
    private val repository: Feat696Repository,
    private val uiMapper: Feat696UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat696UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat696UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat696UserItem1(val user: CoreUser, val label: String)
data class Feat696UserItem2(val user: CoreUser, val label: String)
data class Feat696UserItem3(val user: CoreUser, val label: String)
data class Feat696UserItem4(val user: CoreUser, val label: String)
data class Feat696UserItem5(val user: CoreUser, val label: String)
data class Feat696UserItem6(val user: CoreUser, val label: String)
data class Feat696UserItem7(val user: CoreUser, val label: String)
data class Feat696UserItem8(val user: CoreUser, val label: String)
data class Feat696UserItem9(val user: CoreUser, val label: String)
data class Feat696UserItem10(val user: CoreUser, val label: String)

data class Feat696StateBlock1(val state: Feat696UiModel, val checksum: Int)
data class Feat696StateBlock2(val state: Feat696UiModel, val checksum: Int)
data class Feat696StateBlock3(val state: Feat696UiModel, val checksum: Int)
data class Feat696StateBlock4(val state: Feat696UiModel, val checksum: Int)
data class Feat696StateBlock5(val state: Feat696UiModel, val checksum: Int)
data class Feat696StateBlock6(val state: Feat696UiModel, val checksum: Int)
data class Feat696StateBlock7(val state: Feat696UiModel, val checksum: Int)
data class Feat696StateBlock8(val state: Feat696UiModel, val checksum: Int)
data class Feat696StateBlock9(val state: Feat696UiModel, val checksum: Int)
data class Feat696StateBlock10(val state: Feat696UiModel, val checksum: Int)

fun buildFeat696UserItem(user: CoreUser, index: Int): Feat696UserItem1 {
    return Feat696UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat696StateBlock(model: Feat696UiModel): Feat696StateBlock1 {
    return Feat696StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat696UserSummary> {
    val list = java.util.ArrayList<Feat696UserSummary>(users.size)
    for (user in users) {
        list += Feat696UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat696UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat696UiModel {
    val summaries = (0 until count).map {
        Feat696UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat696UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat696UiModel> {
    val models = java.util.ArrayList<Feat696UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat696AnalyticsEvent1(val name: String, val value: String)
data class Feat696AnalyticsEvent2(val name: String, val value: String)
data class Feat696AnalyticsEvent3(val name: String, val value: String)
data class Feat696AnalyticsEvent4(val name: String, val value: String)
data class Feat696AnalyticsEvent5(val name: String, val value: String)
data class Feat696AnalyticsEvent6(val name: String, val value: String)
data class Feat696AnalyticsEvent7(val name: String, val value: String)
data class Feat696AnalyticsEvent8(val name: String, val value: String)
data class Feat696AnalyticsEvent9(val name: String, val value: String)
data class Feat696AnalyticsEvent10(val name: String, val value: String)

fun logFeat696Event1(event: Feat696AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat696Event2(event: Feat696AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat696Event3(event: Feat696AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat696Event4(event: Feat696AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat696Event5(event: Feat696AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat696Event6(event: Feat696AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat696Event7(event: Feat696AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat696Event8(event: Feat696AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat696Event9(event: Feat696AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat696Event10(event: Feat696AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat696Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat696Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat696Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat696Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat696Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat696Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat696Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat696Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat696Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat696Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat696(u: CoreUser): Feat696Projection1 =
    Feat696Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat696Projection1> {
    val list = java.util.ArrayList<Feat696Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat696(u)
    }
    return list
}
